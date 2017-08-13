package com.m2u.eyelink.collector.common.elasticsearch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

public class RowMapperResultsExtractor<T> implements ResultsExtractor<List<T>> {

    private final RowMapper<T> rowMapper;

    /**
     * Create a new RowMapperResultSetExtractor.
     *
     * @param rowMapper the RowMapper which creates an object for each row
     */
    public RowMapperResultsExtractor(RowMapper<T> rowMapper) {
        Assert.notNull(rowMapper, "RowMapper is required");
        this.rowMapper = rowMapper;
    }

    public List<T> extractData(ResultScanner results) throws Exception {
        List<T> rs = new ArrayList<>();
        int rowNum = 0;
        // FIXME bsh need to implement logic
//        for (Result result : results) {
//            rs.add(this.rowMapper.mapRow(result, rowNum++));
//        }
        return rs;
    }
}
