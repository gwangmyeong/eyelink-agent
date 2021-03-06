package com.m2u.eyelink.thrift.dto;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import org.apache.thrift.EncodingUtils;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;
import org.apache.thrift.scheme.TupleScheme;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2015-6-19")
public class TIntStringValue implements org.apache.thrift.TBase<TIntStringValue, TIntStringValue._Fields>, java.io.Serializable, Cloneable, Comparable<TIntStringValue> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TIntStringValue");

  private static final org.apache.thrift.protocol.TField INT_VALUE_FIELD_DESC = new org.apache.thrift.protocol.TField("intValue", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField STRING_VALUE_FIELD_DESC = new org.apache.thrift.protocol.TField("stringValue", org.apache.thrift.protocol.TType.STRING, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TIntStringValueStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TIntStringValueTupleSchemeFactory());
  }

  private int intValue; // required
  private String stringValue; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    INT_VALUE((short)1, "intValue"),
    STRING_VALUE((short)2, "stringValue");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // INT_VALUE
          return INT_VALUE;
        case 2: // STRING_VALUE
          return STRING_VALUE;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __INTVALUE_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.STRING_VALUE};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.INT_VALUE, new org.apache.thrift.meta_data.FieldMetaData("intValue", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.STRING_VALUE, new org.apache.thrift.meta_data.FieldMetaData("stringValue", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TIntStringValue.class, metaDataMap);
  }

  public TIntStringValue() {
  }

  public TIntStringValue(
    int intValue)
  {
    this();
    this.intValue = intValue;
    setIntValueIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TIntStringValue(TIntStringValue other) {
    __isset_bitfield = other.__isset_bitfield;
    this.intValue = other.intValue;
    if (other.isSetStringValue()) {
      this.stringValue = other.stringValue;
    }
  }

  public TIntStringValue deepCopy() {
    return new TIntStringValue(this);
  }

  @Override
  public void clear() {
    setIntValueIsSet(false);
    this.intValue = 0;
    this.stringValue = null;
  }

  public int getIntValue() {
    return this.intValue;
  }

  public void setIntValue(int intValue) {
    this.intValue = intValue;
    setIntValueIsSet(true);
  }

  public void unsetIntValue() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __INTVALUE_ISSET_ID);
  }

  /** Returns true if field intValue is set (has been assigned a value) and false otherwise */
  public boolean isSetIntValue() {
    return EncodingUtils.testBit(__isset_bitfield, __INTVALUE_ISSET_ID);
  }

  public void setIntValueIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __INTVALUE_ISSET_ID, value);
  }

  public String getStringValue() {
    return this.stringValue;
  }

  public void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }

  public void unsetStringValue() {
    this.stringValue = null;
  }

  /** Returns true if field stringValue is set (has been assigned a value) and false otherwise */
  public boolean isSetStringValue() {
    return this.stringValue != null;
  }

  public void setStringValueIsSet(boolean value) {
    if (!value) {
      this.stringValue = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case INT_VALUE:
      if (value == null) {
        unsetIntValue();
      } else {
        setIntValue((Integer)value);
      }
      break;

    case STRING_VALUE:
      if (value == null) {
        unsetStringValue();
      } else {
        setStringValue((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case INT_VALUE:
      return Integer.valueOf(getIntValue());

    case STRING_VALUE:
      return getStringValue();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case INT_VALUE:
      return isSetIntValue();
    case STRING_VALUE:
      return isSetStringValue();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TIntStringValue)
      return this.equals((TIntStringValue)that);
    return false;
  }

  public boolean equals(TIntStringValue that) {
    if (that == null)
      return false;

    boolean this_present_intValue = true;
    boolean that_present_intValue = true;
    if (this_present_intValue || that_present_intValue) {
      if (!(this_present_intValue && that_present_intValue))
        return false;
      if (this.intValue != that.intValue)
        return false;
    }

    boolean this_present_stringValue = true && this.isSetStringValue();
    boolean that_present_stringValue = true && that.isSetStringValue();
    if (this_present_stringValue || that_present_stringValue) {
      if (!(this_present_stringValue && that_present_stringValue))
        return false;
      if (!this.stringValue.equals(that.stringValue))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_intValue = true;
    list.add(present_intValue);
    if (present_intValue)
      list.add(intValue);

    boolean present_stringValue = true && (isSetStringValue());
    list.add(present_stringValue);
    if (present_stringValue)
      list.add(stringValue);

    return list.hashCode();
  }

  @Override
  public int compareTo(TIntStringValue other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetIntValue()).compareTo(other.isSetIntValue());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIntValue()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.intValue, other.intValue);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetStringValue()).compareTo(other.isSetStringValue());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStringValue()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.stringValue, other.stringValue);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("TIntStringValue(");
    boolean first = true;

    sb.append("intValue:");
    sb.append(this.intValue);
    first = false;
    if (isSetStringValue()) {
      if (!first) sb.append(", ");
      sb.append("stringValue:");
      if (this.stringValue == null) {
        sb.append("null");
      } else {
        sb.append(this.stringValue);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TIntStringValueStandardSchemeFactory implements SchemeFactory {
    public TIntStringValueStandardScheme getScheme() {
      return new TIntStringValueStandardScheme();
    }
  }

  private static class TIntStringValueStandardScheme extends StandardScheme<TIntStringValue> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TIntStringValue struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // INT_VALUE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.intValue = iprot.readI32();
              struct.setIntValueIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // STRING_VALUE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.stringValue = iprot.readString();
              struct.setStringValueIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TIntStringValue struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(INT_VALUE_FIELD_DESC);
      oprot.writeI32(struct.intValue);
      oprot.writeFieldEnd();
      if (struct.stringValue != null) {
        if (struct.isSetStringValue()) {
          oprot.writeFieldBegin(STRING_VALUE_FIELD_DESC);
          oprot.writeString(struct.stringValue);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TIntStringValueTupleSchemeFactory implements SchemeFactory {
    public TIntStringValueTupleScheme getScheme() {
      return new TIntStringValueTupleScheme();
    }
  }

  private static class TIntStringValueTupleScheme extends TupleScheme<TIntStringValue> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TIntStringValue struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetIntValue()) {
        optionals.set(0);
      }
      if (struct.isSetStringValue()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetIntValue()) {
        oprot.writeI32(struct.intValue);
      }
      if (struct.isSetStringValue()) {
        oprot.writeString(struct.stringValue);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TIntStringValue struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.intValue = iprot.readI32();
        struct.setIntValueIsSet(true);
      }
      if (incoming.get(1)) {
        struct.stringValue = iprot.readString();
        struct.setStringValueIsSet(true);
      }
    }
  }

}

