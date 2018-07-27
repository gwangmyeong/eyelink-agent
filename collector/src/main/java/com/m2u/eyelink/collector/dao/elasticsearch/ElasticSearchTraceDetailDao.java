package com.m2u.eyelink.collector.dao.elasticsearch;

import static com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchTables.TYPE_TRACE_DETAIL;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.m2u.eyelink.collector.bo.AnnotationBo;
import com.m2u.eyelink.collector.bo.ApiMetaDataBo;
import com.m2u.eyelink.collector.bo.MethodTypeEnum;
import com.m2u.eyelink.collector.bo.SpanBo;
import com.m2u.eyelink.collector.bo.SpanEventBo;
import com.m2u.eyelink.collector.bo.SqlMetaDataBo;
import com.m2u.eyelink.collector.bo.StringMetaDataBo;
import com.m2u.eyelink.collector.common.elasticsearch.ElasticSearchOperations2;
import com.m2u.eyelink.collector.common.elasticsearch.span.CallTree;
import com.m2u.eyelink.collector.common.elasticsearch.span.CallTreeIterator;
import com.m2u.eyelink.collector.common.elasticsearch.span.SpanAlign;
import com.m2u.eyelink.collector.common.elasticsearch.span.SpanAligner2;
import com.m2u.eyelink.collector.common.elasticsearch.span.SpanResult;
import com.m2u.eyelink.collector.dao.ApiMetaDataDao;
import com.m2u.eyelink.collector.dao.SqlMetaDataDao;
import com.m2u.eyelink.collector.dao.StringMetaDataDao;
import com.m2u.eyelink.collector.dao.TraceDetailDao;
import com.m2u.eyelink.collector.dao.elasticsearch.filter.MetaDataFilter;
import com.m2u.eyelink.collector.dao.elasticsearch.filter.MetaDataFilter.MetaData;
import com.m2u.eyelink.collector.util.AnnotationUtils;
import com.m2u.eyelink.collector.util.ElasticSearchUtils;
import com.m2u.eyelink.collector.util.IntStringStringValue;
import com.m2u.eyelink.collector.util.OutputParameterParser;
import com.m2u.eyelink.context.DefaultSqlParser;
import com.m2u.eyelink.common.trace.AnnotationKey;
import com.m2u.eyelink.util.AnnotationKeyUtils;
import com.m2u.eyelink.util.SqlParser;
import com.m2u.eyelink.util.TransactionId;

@Repository
public class ElasticSearchTraceDetailDao implements TraceDetailDao {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ElasticSearchOperations2 elasticSearchTemplate;

    @Autowired
    private ApiMetaDataDao apiMetaDataDao;

//  @Autowired
    private SqlMetaDataDao sqlMetaDataDao;
    
    @Autowired
    private StringMetaDataDao stringMetaDataDao;
    
    @Autowired(required=false)
    private MetaDataFilter metaDataFilter;

    private final SqlParser sqlParser = new DefaultSqlParser();
    private final OutputParameterParser outputParameterParser = new OutputParameterParser();

	@Override
	public void insert(SpanBo spanBo) {
		if (spanBo == null) {
			throw new NullPointerException("spanBo must not be null");
		}

		long acceptedTime = spanBo.getCollectorAcceptTime();

		TransactionId transactionId = spanBo.getTransactionId();

		// add by bsh
		List<SpanBo> listSpanBo = new ArrayList<SpanBo>();
		listSpanBo.add(spanBo);
        final SpanResult result = order(listSpanBo, acceptedTime);
        final CallTreeIterator callTreeIterator = result.getCallTree();
        final List<SpanAlign> values = callTreeIterator.values();

        transitionDynamicApiId(values);
        transitionSqlId(values);
        transitionCachedString(values);
        transitionException(values);
        addElaspedTime(values);
        
		boolean success = elasticSearchTemplate.asyncPut(ElasticSearchUtils.generateIndexName(spanBo.getAgentId(), TYPE_TRACE_DETAIL),
				TYPE_TRACE_DETAIL, values.get(0).getMap());
		if (!success) {
			elasticSearchTemplate.put(ElasticSearchUtils.generateIndexName(spanBo.getAgentId(), TYPE_TRACE_DETAIL), TYPE_TRACE_DETAIL,
					values.get(0).getMap());
		}

	}


	private SpanResult order(List<SpanBo> spans, long selectedSpanHint) {
        SpanAligner2 spanAligner = new SpanAligner2(spans, selectedSpanHint);
        final CallTree callTree = spanAligner.sort();

        return new SpanResult(spanAligner.getMatchType(), callTree.iterator());
    }
    
    private void transitionAnnotation(List<SpanAlign> spans, AnnotationReplacementCallback annotationReplacementCallback) {
        for (SpanAlign spanAlign : spans) {
            List<AnnotationBo> annotationBoList = spanAlign.getAnnotationBoList();
            if (annotationBoList == null) {
                annotationBoList = new ArrayList<>();
                spanAlign.setAnnotationBoList(annotationBoList);
            }
            annotationReplacementCallback.replacement(spanAlign, annotationBoList);
        }
    }
    
    private void transitionDynamicApiId(List<SpanAlign> spans) {
        this.transitionAnnotation(spans, new AnnotationReplacementCallback() {
            @Override
            public void replacement(SpanAlign spanAlign, List<AnnotationBo> annotationBoList) {

                final int apiId = spanAlign.getApiId();
                if (apiId == 0) {
                    String apiString = AnnotationUtils.findApiAnnotation(annotationBoList);
                    // annotation base api
                    if (apiString != null) {
                        ApiMetaDataBo apiMetaDataBo = new ApiMetaDataBo(spanAlign.getAgentId(), spanAlign.getStartTime(), apiId);
                        apiMetaDataBo.setApiInfo(apiString);
                        apiMetaDataBo.setLineNumber(-1);
                        apiMetaDataBo.setMethodTypeEnum(MethodTypeEnum.DEFAULT);

                        AnnotationBo apiAnnotation = new AnnotationBo();
                        apiAnnotation.setKey(AnnotationKey.API_METADATA.getCode());
                        apiAnnotation.setValue(apiMetaDataBo);
                        annotationBoList.add(apiAnnotation);
                        return;
                    }
                }

                // may be able to get a more accurate data using agentIdentifier.
                List<ApiMetaDataBo> apiMetaDataList = apiMetaDataDao.getApiMetaData(spanAlign.getAgentId(), spanAlign.getAgentStartTime(), apiId);
                int size = apiMetaDataList.size();
                if (size == 0) {
                    AnnotationBo api = new AnnotationBo();
                    api.setKey(AnnotationKey.ERROR_API_METADATA_NOT_FOUND.getCode());
                    api.setValue("API-DynamicID not found. api:" + apiId);
                    annotationBoList.add(api);
                } else if (size == 1) {
                    ApiMetaDataBo apiMetaDataBo = apiMetaDataList.get(0);
                    AnnotationBo apiMetaData = new AnnotationBo();
                    apiMetaData.setKey(AnnotationKey.API_METADATA.getCode());
                    apiMetaData.setValue(apiMetaDataBo);
                    annotationBoList.add(apiMetaData);

                    if (apiMetaDataBo.getMethodTypeEnum() == MethodTypeEnum.DEFAULT) {

                        AnnotationBo apiAnnotation = new AnnotationBo();
                        apiAnnotation.setKey(AnnotationKey.API.getCode());
                        String apiInfo = getApiInfo(apiMetaDataBo);
                        apiAnnotation.setValue(apiInfo);
                        annotationBoList.add(apiAnnotation);
                    } else {
                        AnnotationBo apiAnnotation = new AnnotationBo();
                        apiAnnotation.setKey(AnnotationKey.API_TAG.getCode());
                        apiAnnotation.setValue(getApiTagInfo(apiMetaDataBo));
                        annotationBoList.add(apiAnnotation);
                    }
                } else {
                    AnnotationBo apiAnnotation = new AnnotationBo();
                    apiAnnotation.setKey(AnnotationKey.ERROR_API_METADATA_DID_COLLSION.getCode());
                    String collisionMessage = collisionApiDidMessage(apiId, apiMetaDataList);
                    apiAnnotation.setValue(collisionMessage);
                    annotationBoList.add(apiAnnotation);
                }

            }

        });
    }
    
    private void transitionSqlId(final List<SpanAlign> spans) {
        this.transitionAnnotation(spans, new AnnotationReplacementCallback() {
            @Override
            public void replacement(SpanAlign spanAlign, List<AnnotationBo> annotationBoList) {
                AnnotationBo sqlIdAnnotation = findAnnotation(annotationBoList, AnnotationKey.SQL_ID.getCode());
                if (sqlIdAnnotation == null) {
                    return;
                }
                if (metaDataFilter != null && metaDataFilter.filter(spanAlign, MetaData.SQL)) {
                    AnnotationBo annotationBo = metaDataFilter.createAnnotationBo(spanAlign, MetaData.SQL);
                    annotationBoList.add(annotationBo);
                    return;
                }

                // value of sqlId's annotation contains multiple values.
                final IntStringStringValue sqlValue = (IntStringStringValue) sqlIdAnnotation.getValue();
                final int sqlId = sqlValue.getIntValue();
                final String sqlParam = sqlValue.getStringValue1();
                final List<SqlMetaDataBo> sqlMetaDataList = sqlMetaDataDao.getSqlMetaData(spanAlign.getAgentId(), spanAlign.getAgentStartTime(), sqlId);
                final int size = sqlMetaDataList.size();
                if (size == 0) {
                    AnnotationBo api = new AnnotationBo();
                    api.setKey(AnnotationKey.SQL.getCode());
                    api.setValue("SQL-ID not found sqlId:" + sqlId);
                    annotationBoList.add(api);
                } else if (size == 1) {
                    final SqlMetaDataBo sqlMetaDataBo = sqlMetaDataList.get(0);
                    if (StringUtils.isEmpty(sqlParam)) {
                        AnnotationBo sqlMeta = new AnnotationBo();
                        sqlMeta.setKey(AnnotationKey.SQL_METADATA.getCode());
                        sqlMeta.setValue(sqlMetaDataBo.getSql());
                        annotationBoList.add(sqlMeta);

//                        AnnotationBo checkFail = checkIdentifier(spanAlign, sqlMetaDataBo);
//                        if (checkFail != null) {
//                            // fail
//                            annotationBoList.add(checkFail);
//                            return;
//                        }

                        AnnotationBo sql = new AnnotationBo();
                        sql.setKey(AnnotationKey.SQL.getCode());
                        sql.setValue(sqlMetaDataBo.getSql().trim());
                        annotationBoList.add(sql);
                    } else {
                        logger.debug("sqlMetaDataBo:{}", sqlMetaDataBo);
                        final String outputParams = sqlParam;
                        List<String> parsedOutputParams = outputParameterParser.parseOutputParameter(outputParams);
                        logger.debug("outputPrams:{}, parsedOutputPrams:{}", outputParams, parsedOutputParams);
                        String originalSql = sqlParser.combineOutputParams(sqlMetaDataBo.getSql(), parsedOutputParams);
                        logger.debug("outputPrams{}, originalSql:{}", outputParams, originalSql);

                        AnnotationBo sqlMeta = new AnnotationBo();
                        sqlMeta.setKey(AnnotationKey.SQL_METADATA.getCode());
                        sqlMeta.setValue(sqlMetaDataBo.getSql());
                        annotationBoList.add(sqlMeta);


                        AnnotationBo sql = new AnnotationBo();
                        sql.setKey(AnnotationKey.SQL.getCode());
                        sql.setValue(originalSql.trim());
                        annotationBoList.add(sql);

                    }
                } else {
                    // TODO need improvement
                    AnnotationBo api = new AnnotationBo();
                    api.setKey(AnnotationKey.SQL.getCode());
                    api.setValue(collisionSqlIdCodeMessage(sqlId, sqlMetaDataList));
                    annotationBoList.add(api);
                }
                // add if bindValue exists
                final String bindValue = sqlValue.getStringValue2();
                if (StringUtils.isNotEmpty(bindValue)) {
                    AnnotationBo bindValueAnnotation = new AnnotationBo();
                    bindValueAnnotation.setKey(AnnotationKey.SQL_BINDVALUE.getCode());
                    bindValueAnnotation.setValue(bindValue);
                    annotationBoList.add(bindValueAnnotation);
                }

            }

        });
    }
    
    private void transitionCachedString(List<SpanAlign> spans) {
        this.transitionAnnotation(spans, new AnnotationReplacementCallback() {
            @Override
            public void replacement(SpanAlign spanAlign, List<AnnotationBo> annotationBoList) {

                List<AnnotationBo> cachedStringAnnotation = findCachedStringAnnotation(annotationBoList);
                if (cachedStringAnnotation.isEmpty()) {
                    return;
                }
                for (AnnotationBo annotationBo : cachedStringAnnotation) {
                    final int cachedArgsKey = annotationBo.getKey();
                    int stringMetaDataId = (Integer) annotationBo.getValue();
                    List<StringMetaDataBo> stringMetaList = stringMetaDataDao.getStringMetaData(spanAlign.getAgentId(), spanAlign.getAgentStartTime(), stringMetaDataId);
                    int size = stringMetaList.size();
                    if (size == 0) {
                        logger.warn("StringMetaData not Found {}/{}/{}", spanAlign.getAgentId(), stringMetaDataId, spanAlign.getAgentStartTime());
                        AnnotationBo api = new AnnotationBo();
                        api.setKey(AnnotationKey.ERROR_API_METADATA_NOT_FOUND.getCode());
                        api.setValue("CACHED-STRING-ID not found. stringId:" + cachedArgsKey);
                        annotationBoList.add(api);
                    } else if (size >= 1) {
                        // key collision shouldn't really happen (probability too low)
                        StringMetaDataBo stringMetaDataBo = stringMetaList.get(0);

                        AnnotationBo stringMetaData = new AnnotationBo();
                        stringMetaData.setKey(AnnotationKeyUtils.cachedArgsToArgs(cachedArgsKey));
                        stringMetaData.setValue(stringMetaDataBo.getStringValue());
                        annotationBoList.add(stringMetaData);
                        if (size > 1) {
                            logger.warn("stringMetaData size not 1 :{}", stringMetaList);
                        }
                    }
                }
            }

        });
    }
    
    private void transitionException(List<SpanAlign> spanAlignList) {
    		boolean isFirst = true;
        for (SpanAlign spanAlign : spanAlignList) {
            if (spanAlign.hasException()) {
                StringMetaDataBo stringMetaData = selectStringMetaData(spanAlign.getAgentId(), spanAlign.getExceptionId(), spanAlign.getAgentStartTime());
                spanAlign.setExceptionClass(stringMetaData.getStringValue());
                if (isFirst) {
                		spanAlignList.get(0).getSpanBo().setExceptionInfo(spanAlign.getExceptionId(), spanAlign.getExceptionMessage());
                		isFirst = false;
                }
            }
        }

    }
    
    private void addElaspedTime(List<SpanAlign> spanAlignList) {
    		// spanAlignList내 각 배열의 SpanEventBo에는 gap, self-exectime, elasped time 계산값을가지고 있음.
    		// spanAlignList 배열의 첫번째의 SpanAlign내 spanBo -> spanEventBoList의  해당배열내 spanEventBo에 gap, self-exectime elasped time을 추가
    		// db에 기록시 spanAlignList 배열값에서 첫번째 SpanAlign만 저장한다. 다른 배열은 중복된 값임.. 
    		// TODO bsh, 위 로직 확인 필요
    		int i = 0;
    		int totalElasped = 0;
        for (SpanAlign spanAlign : spanAlignList) {
        		if (i == 0) {
           		 totalElasped = spanAlignList.get(0).getSpanBo().getElapsed();        			
        		}
        		// FIXME, bsh, change to elapsed time logic when exist slibling
            if (spanAlign.getSpanEventBo() != null) {
            		 totalElasped = (int) (totalElasped - spanAlign.getExecutionMilliseconds());
            		// getSpanEventBoList에서 i-1 -< spanAlignList에는 getSpanEventBoList의 2번째 값부터 기록되어 있음.
            		spanAlignList.get(0).getSpanBo().getSpanEventBoList().get(i-1).setGap(spanAlign.getGap());
            		spanAlignList.get(0).getSpanBo().getSpanEventBoList().get(i-1).setExecutionMilliseconds(spanAlign.getExecutionMilliseconds());
            		spanAlignList.get(0).getSpanBo().getSpanEventBoList().get(i-1).setElapsed(totalElasped);
            }
            i++;
        }
 	}

    private StringMetaDataBo selectStringMetaData(String agentId, int cacheId, long agentStartTime) {
        final List<StringMetaDataBo> metaDataList = stringMetaDataDao.getStringMetaData(agentId, agentStartTime, cacheId);
        if (metaDataList == null || metaDataList.isEmpty()) {
            logger.warn("StringMetaData not Found agent:{}, cacheId{}, agentStartTime:{}", agentId, cacheId, agentStartTime);
            StringMetaDataBo stringMetaDataBo = new StringMetaDataBo(agentId, agentStartTime, cacheId);
            stringMetaDataBo.setStringValue("STRING-META-DATA-NOT-FOUND");
            return stringMetaDataBo;
        }
        if (metaDataList.size() == 1) {
            return metaDataList.get(0);
        } else {
            logger.warn("stringMetaData size not 1 :{}", metaDataList);
            return metaDataList.get(0);
        }
    }
    
    private List<AnnotationBo> findCachedStringAnnotation(List<AnnotationBo> annotationBoList) {
        List<AnnotationBo> findAnnotationBoList = new ArrayList<>(annotationBoList.size());
        for (AnnotationBo annotationBo : annotationBoList) {
            if (AnnotationKeyUtils.isCachedArgsKey(annotationBo.getKey())) {
                findAnnotationBoList.add(annotationBo);
            }
        }
        return findAnnotationBoList;
    }

    private String collisionSqlIdCodeMessage(int sqlId, List<SqlMetaDataBo> sqlMetaDataList) {
        // TODO need a separate test case to test for hashCode collision (probability way too low for easy replication)
        StringBuilder sb = new StringBuilder(64);
        sb.append("Collision Sql sqlId:");
        sb.append(sqlId);
        sb.append('\n');
        for (int i = 0; i < sqlMetaDataList.size(); i++) {
            if (i != 0) {
                sb.append("or\n");
            }
            SqlMetaDataBo sqlMetaDataBo = sqlMetaDataList.get(i);
            sb.append(sqlMetaDataBo.getSql());
        }
        return sb.toString();
    }

    private String getApiInfo(ApiMetaDataBo apiMetaDataBo) {
        if (apiMetaDataBo.getLineNumber() != -1) {
            return apiMetaDataBo.getApiInfo() + ":" + apiMetaDataBo.getLineNumber();
        } else {
            return apiMetaDataBo.getApiInfo();
        }
    }
    
    private String getApiTagInfo(ApiMetaDataBo apiMetaDataBo) {
        return apiMetaDataBo.getApiInfo();
    }

    private String collisionApiDidMessage(int apidId, List<ApiMetaDataBo> apiMetaDataList) {
        // TODO need a separate test case to test for apidId collision (probability way too low for easy replication)
        StringBuilder sb = new StringBuilder(64);
        sb.append("Collision Api DynamicId:");
        sb.append(apidId);
        sb.append('\n');
        for (int i = 0; i < apiMetaDataList.size(); i++) {
            if (i != 0) {
                sb.append("or\n");
            }
            ApiMetaDataBo apiMetaDataBo = apiMetaDataList.get(i);
            sb.append(getApiInfo(apiMetaDataBo));
        }
        return sb.toString();
    }

    public interface AnnotationReplacementCallback {
        void replacement(SpanAlign spanAlign, List<AnnotationBo> annotationBoList);
    }
    
    private AnnotationBo findAnnotation(List<AnnotationBo> annotationBoList, int key) {
        for (AnnotationBo annotationBo : annotationBoList) {
            if (key == annotationBo.getKey()) {
                return annotationBo;
            }
        }
        return null;
    }

}
