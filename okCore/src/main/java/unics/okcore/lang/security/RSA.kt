/**
 * Created by Lucio on 2022/2/12.
 */
package unics.okcore.lang.security

inline fun ByteArray.toRSAEncrypt(
    pubKey: ByteArray,
    transformation: String = RSAUtils.RSA_TRANSFORMATION_NONE
): ByteArray = RSAUtils.encrypt(this, pubKey, transformation)

inline fun String.toRSAEncrypt(
    pubKey: String,
    transformation: String = RSAUtils.RSA_TRANSFORMATION_NONE
): ByteArray = RSAUtils.encrypt(this, pubKey, transformation)

inline fun String.toRSAEncryptString(
    pubKey: String,
    transformation: String = RSAUtils.RSA_TRANSFORMATION_NONE
): String = RSAUtils.encryptToString(this, pubKey, transformation)


inline fun ByteArray.toRSADecrypt(
    privateKey: ByteArray,
    transformation: String = RSAUtils.RSA_TRANSFORMATION_NONE
): ByteArray = RSAUtils.decrypt(this, privateKey, transformation)

inline fun String.toRSADecrypt(
    privateKey: String,
    transformation: String = RSAUtils.RSA_TRANSFORMATION_NONE
): ByteArray = RSAUtils.decrypt(this, privateKey, transformation)

inline fun String.toRSADecryptString(
    privateKey: String,
    transformation: String = RSAUtils.RSA_TRANSFORMATION_NONE
): String = RSAUtils.decryptToString(this, privateKey, transformation)
