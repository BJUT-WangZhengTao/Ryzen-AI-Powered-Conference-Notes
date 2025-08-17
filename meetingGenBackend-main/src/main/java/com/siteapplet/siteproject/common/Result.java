package com.siteapplet.siteproject.common;

/**
 * @author: danghongbo
 * ===============================
 * Created with IDEA
 * Date: 2024/2/28
 * Time: 15:52
 * ===============================
 */
//public class Result {
//    public class Result {
//        private Object data;
//        private String msg;
//        private int code;
//
//        // getter setter 省略，构造方法省略
//        // 操作成功返回数据
//        public static Result succ(Object data) {
//            return succ(200, "操作成功", data);
//        }
//
//        public static Result succ(String msg) {
//            return succ(200, msg, null);
//        }
//
//
//        public static Result succ(int code, String msg, Object data) {
//            Result r = new Result();
//            r.setCode(code);
//            r.setMsg(msg);
//            r.setData(data);
//            return r;
//        }
//
//        private void setCode(int code) {
//            this.code = code;
//        }
//
//        public static Result succ(String msg, Object data) {
//            return succ(200,msg,data);
//        }
//
//        // 操作异常返回
//        public static Result fail(int code, String msg, Object data) {
//            Result r = new Result();
//            r.setCode(code);
//            r.setMsg(msg);
//            r.setData(data);
//            return r;
//        }
//
//        private void setData(Object data) {
//            this.data = data;
//        }
//
//        private void setMsg(String msg) {
//            this.msg = msg;
//        }
//
//        public static Result fail(String msg) {
//            return fail(400,msg,null);
//        }
//
//        public static Result fail(int code, String msg) {
//            return fail(code,msg,"null");
//        }
//
//        public static Result fail(String msg, Object data) {
//            return fail(400,msg,data);
//        }
//    }
//}
