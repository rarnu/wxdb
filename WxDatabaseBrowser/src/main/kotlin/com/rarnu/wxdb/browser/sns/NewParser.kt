package com.rarnu.wxdb.browser.sns

import java.lang.reflect.Array
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType

class NewParser(private val bArr: ByteArray, private val clz: Class<*>?) {

    fun parseFrom(): ParseInfo {
        val ctx = clz?.newInstance()
        val mLoad = clz?.superclass?.getDeclaredMethod("parseFrom", ByteArray::class.java)
        mLoad?.isAccessible = true
        val obj = mLoad?.invoke(ctx, bArr)
        val root = ParseInfo()
        parser(obj, clz, root)
        return root
    }

    private fun parser(obj: Any?, clz: Class<*>?, root: ParseInfo) {
        if (obj != null) {
            root.fieldType = clz?.name
            root.fieldValue = obj.toString()
            root.fieldName = ""
            val fields = clz?.declaredFields ?: return
            for (field in fields) {
                field.isAccessible = true
                val fieldName = field.name
                val child = ParseInfo()
                root.childList.add(child)
                val fieldValue = field.get(obj) ?: continue
                child.fieldValue = "$fieldValue"
                child.fieldType = field.type.name
                child.fieldName = fieldName
                if (field.type.name.endsWith("List")) {
                    parseListType(field, obj, child)
                } else if (field.type.name.endsWith("Array")) {
                    parseArrayType(field, child)
                } else if (!isCommonType(field.type.name)) {
                    parser(fieldValue, fieldValue.javaClass, child)
                }
            }

        }
    }

    private fun isCommonType(type: String) = !type.contains("com.tencent.mm")

    private fun parseArrayType(f: Field, root: ParseInfo) {
        val t = f.genericType
        if (t is ParameterizedType) {
            val clz = t.actualTypeArguments[0] as Class<*>
            val size = Array.getLength(f)
            for (i in 0 until size) {
                val child = ParseInfo()
                child.fieldType = clz.name
                child.fieldName = "$i"
                val value = Array.get(f, i)
                child.fieldValue = "$value"
                root.childList.add(child)
                parser(value, clz, child)
            }
        }
    }

    private fun parseListType(f: Field, obj: Any?, root: ParseInfo) {
        val t = f.genericType
        if (t is ParameterizedType) {
            val clz = t.actualTypeArguments[0] as Class<*>
            val clzz = f.get(obj).javaClass
            val m = clzz.getDeclaredMethod("size")
            val size = m.invoke(f.get(obj)) as Int
            for (i in 0 until size) {
                val child = ParseInfo()
                child.fieldType = clz.name
                child.fieldName = "$i"
                val getM = clzz.getDeclaredMethod("get", Int::class.javaPrimitiveType)
                getM.isAccessible = true
                val listValue = getM.invoke(f.get(obj), i)
                child.fieldValue = "$listValue"
                root.childList.add(child)
                parser(listValue, clz, child)
            }
        }
    }
}