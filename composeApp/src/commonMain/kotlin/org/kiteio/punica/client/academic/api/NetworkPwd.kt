package org.kiteio.punica.client.academic.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.client.academic.AcademicSystem

/**
 * 返回校园网默认密码（身份证后 8 位）。
 */
suspend fun AcademicSystem.getNetworkPwd(): String {
    return withContext(Dispatchers.Default) {
        // ⚠️ API 中包含许多关键个人信息（学籍卡片）
        val text = get("jsxsd/grxx/xsxx").bodyAsText()

        val doc = Ksoup.parse(text)
        val tds = doc.getElementsByTag("td")

        // 下标为 172 的元素是身份证号（普遍性待考量）
        return@withContext tds[172].text().trim().substring(10)
    }
}