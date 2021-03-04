package net.arwix.spaceweather.library.common.retrofit2converter
//
//import kotlinx.serialization.*
//import okhttp3.MediaType
//import okhttp3.RequestBody
//import okhttp3.ResponseBody
//
//internal sealed class Serializer {
//    abstract fun <T> fromResponseBody(loader: DeserializationStrategy<T>, body: ResponseBody): T
//    abstract fun <T> toRequestBody(contentType: MediaType, saver: SerializationStrategy<T>, value: T): RequestBody
//
//    class FromString(private val format: StringFormat) : Serializer() {
//        override fun <T> fromResponseBody(loader: DeserializationStrategy<T>, body: ResponseBody): T {
//            val string = body.string()
//            return format.decodeFromString(loader, string)
//        }
//
//        override fun <T> toRequestBody(contentType: MediaType, saver: SerializationStrategy<T>, value: T): RequestBody {
//            val string = format.encodeToString(saver, value)
//            return RequestBody.create(contentType, string)
//        }
//    }
//
//    class FromBytes(private val format: BinaryFormat): Serializer() {
//        override fun <T> fromResponseBody(loader: DeserializationStrategy<T>, body: ResponseBody): T {
//            val bytes = body.bytes()
//            return format.decodeFromByteArray(loader, bytes)
//        }
//
//        override fun <T> toRequestBody(contentType: MediaType, saver: SerializationStrategy<T>, value: T): RequestBody {
//            val bytes = format.encodeToByteArray(saver, value)
//            return RequestBody.create(contentType, bytes)
//        }
//    }
//}