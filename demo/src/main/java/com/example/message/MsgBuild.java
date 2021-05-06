package com.example.message;

import cn.hutool.core.util.StrUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class MsgBuild {
    public static byte[] build(Map<String, Object> map) {
        if (map.isEmpty()) {
            return null;
        }

        byte[] ret = buildContent(map);
        if (ret != null && ret.length > 0) {
            byte[] tag = MsgUtil.intToByte(MsgType.MAP, 1);
            byte[] size = MsgUtil.intToByte(ret.length, 4);
            ret = MsgUtil.concat(tag, size, ret);
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    private static byte[] buildContent(Map<String, Object> map) {
        byte[] ret = new byte[]{};
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (StrUtil.isEmptyIfStr(key) || StrUtil.isEmptyIfStr(value)) {
                continue;
            }
            int iTag = Integer.parseInt(key);
            byte[] tag = MsgUtil.intToByte(iTag, 4);
            ret = MsgUtil.concat(ret, tag);
            if (value instanceof Boolean) {
                Boolean val = (Boolean) value;
                ret = MsgUtil.concat(ret, setBool(val));
            } else if (value instanceof Byte) {
                Byte val = (Byte) value;
                ret = MsgUtil.concat(ret, setByte(val));
            } else if (value instanceof Character) {
                Character val = (Character) value;
                ret = MsgUtil.concat(ret, setCharacter(val));
            } else if (value instanceof Integer) {
                Integer val = (Integer) value;
                ret = MsgUtil.concat(ret, setInteger(val));
            } else if (value instanceof Long) {
                Long val = (Long) value;
                ret = MsgUtil.concat(ret, setLong(val));
            } else if (value instanceof Float) {
                Float val = (Float) value;
                ret = MsgUtil.concat(ret, setFloat(val));
            } else if (value instanceof Double) {
                Double val = (Double) value;
                ret = MsgUtil.concat(ret, setDouble(val));
            } else if (value instanceof BigDecimal) {
                BigDecimal val = (BigDecimal) value;
                ret = MsgUtil.concat(ret, setBigDecimal(val));
            } else if (value instanceof String) {
                String val = (String) value;
                ret = MsgUtil.concat(ret, setString(val));
            } else if (value instanceof Byte[]) {
                Byte[] val = (Byte[]) value;
                ret = MsgUtil.concat(ret, setBytes(val));
            } else if (value instanceof List) {
                List<Object> list = (List<Object>) value;
                byte[] val = buildContent(list);
                if (val == null || val.length < 1) {
                    continue;
                }
                byte[] tTag = MsgUtil.intToByte(MsgType.LIST, 1);
                byte[] size = MsgUtil.intToByte(val.length, 4);
                ret = MsgUtil.concat(ret, tTag, size, val);
            } else if (value instanceof Map) {
                Map<String, Object> m = (Map<String, Object>) value;
                byte[] val = buildContent(m);
                if (val == null || val.length < 1) {
                    continue;
                }
                byte[] tTag = MsgUtil.intToByte(MsgType.MAP, 1);
                byte[] size = MsgUtil.intToByte(val.length, 4);
                ret = MsgUtil.concat(ret, tTag, size, val);
            } else {

            }
        }

        return ret;
    }

    @SuppressWarnings("unchecked")
    private static byte[] buildContent(List<Object> list) {
        byte[] ret = new byte[]{};
        if (list == null || list.isEmpty()) {
            return ret;
        }
        for (Object value : list) {
            if (value == null) {
                continue;
            }
            if (value instanceof Boolean) {
                Boolean val = (Boolean) value;
                ret = MsgUtil.concat(ret, setBool(val));
            } else if (value instanceof Byte) {
                Byte val = (Byte) value;
                ret = MsgUtil.concat(ret, setByte(val));
            } else if (value instanceof Character) {
                Character val = (Character) value;
                ret = MsgUtil.concat(ret, setCharacter(val));
            } else if (value instanceof Integer) {
                Integer val = (Integer) value;
                ret = MsgUtil.concat(ret, setInteger(val));
            } else if (value instanceof Long) {
                Long val = (Long) value;
                ret = MsgUtil.concat(ret, setLong(val));
            } else if (value instanceof Float) {
                Float val = (Float) value;
                ret = MsgUtil.concat(ret, setFloat(val));
            } else if (value instanceof Double) {
                Double val = (Double) value;
                ret = MsgUtil.concat(ret, setDouble(val));
            } else if (value instanceof BigDecimal) {
                BigDecimal val = (BigDecimal) value;
                ret = MsgUtil.concat(ret, setBigDecimal(val));
            } else if (value instanceof String) {
                String val = (String) value;
                ret = MsgUtil.concat(ret, setString(val));
            } else if (value instanceof Byte[]) {
                Byte[] val = (Byte[]) value;
                ret = MsgUtil.concat(ret, setBytes(val));
            } else if (value instanceof List) {
                List<Object> tList = (List<Object>) value;
                byte[] val = buildContent(tList);
                if (val == null || val.length < 1) {
                    continue;
                }
                byte[] tag = MsgUtil.intToByte(MsgType.LIST, 1);
                byte[] size = MsgUtil.intToByte(val.length, 4);
                ret = MsgUtil.concat(ret, tag, size, val);
            } else if (value instanceof Map) {
                Map<String, Object> m = (Map<String, Object>) value;
                byte[] val = buildContent(m);
                if (val == null || val.length < 1) {
                    continue;
                }
                byte[] tag = MsgUtil.intToByte(MsgType.MAP, 1);
                byte[] size = MsgUtil.intToByte(val.length, 4);
                ret = MsgUtil.concat(ret, tag, size, val);
            } else {

            }
        }
        return ret;
    }

    private static byte[] setBool(Boolean b) {
        if (b != null) {
            int i = b.booleanValue() ? 1 : 0;
            byte[] tag = MsgUtil.intToByte(MsgType.BOOL, 1);
            byte[] val = MsgUtil.intToByte(i, 1);
            return MsgUtil.concat(tag, val);
        }
        return null;
    }

    private static byte[] setByte(Byte b) {
        if (b != null) {
            byte[] tag = MsgUtil.intToByte(MsgType.INT8, 1);
            byte[] val = new byte[]{b.byteValue()};
            return MsgUtil.concat(tag, val);
        }
        return null;
    }

    private static byte[] setCharacter(Character c) {
        if (c != null) {
            byte[] tag = MsgUtil.intToByte(MsgType.INT16, 1);
            byte[] val = MsgUtil.charToByte(new char[]{c.charValue()});
            return MsgUtil.concat(tag, val);
        }
        return null;
    }

    private static byte[] setInteger(Integer integer) {
        if (integer != null) {
            byte[] tag = MsgUtil.intToByte(MsgType.INT32, 1);
            byte[] val = MsgUtil.intToByte(integer.intValue(), 4);
            return MsgUtil.concat(tag, val);
        }
        return null;
    }

    private static byte[] setLong(Long l) {
        if (l != null) {
            byte[] tag = MsgUtil.intToByte(MsgType.INT64, 1);
            byte[] val = MsgUtil.longToByte(l.longValue());
            return MsgUtil.concat(tag, val);
        }
        return null;
    }

    private static byte[] setFloat(Float f) {
        if (f != null) {
            byte[] tag = MsgUtil.intToByte(MsgType.FLOAT, 1);
            byte[] val = MsgUtil.floatToByte(f.floatValue());
            return MsgUtil.concat(tag, val);
        }
        return null;
    }

    private static byte[] setDouble(Double d) {
        if (d != null) {
            byte[] tag = MsgUtil.intToByte(MsgType.DOUBLE, 1);
            byte[] val = MsgUtil.doubleToByte(d.doubleValue());
            return MsgUtil.concat(tag, val);
        }
        return null;
    }

    private static byte[] setBigDecimal(BigDecimal bd) {
        if (bd != null) {
            byte[] tag = MsgUtil.intToByte(MsgType.DOUBLE, 1);
            byte[] val = MsgUtil.doubleToByte(bd.doubleValue());
            return MsgUtil.concat(tag, val);
        }
        return null;
    }

    private static byte[] setString(String str) {
        if (StrUtil.isEmptyIfStr(str)) {
            str = "";
        }
        byte[] tag = MsgUtil.intToByte(MsgType.STRING, 1);
        byte[] val = str.getBytes();
        val = MsgUtil.concat(val, MsgUtil.charToByte(new char[]{'\0'}));
        byte[] size = MsgUtil.intToByte(val.length, 4);
        return MsgUtil.concat(tag, size, val);
    }

    private static byte[] setBytes(Byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            byte[] val = new byte[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                val[i] = bytes[i].byteValue();
            }
            byte[] tag = MsgUtil.intToByte(MsgType.RAW, 1);
            byte[] size = MsgUtil.intToByte(val.length, 4);
            return MsgUtil.concat(tag, size, val);
        }
        return null;
    }
}
