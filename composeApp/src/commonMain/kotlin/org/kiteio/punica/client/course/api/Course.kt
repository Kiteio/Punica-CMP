package org.kiteio.punica.client.course.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kiteio.punica.client.academic.foundation.MCourse
import org.kiteio.punica.client.course.CourseSystem

/**
 * 返回已选课程列表。
 */
suspend fun CourseSystem.getCourses(): List<MCourse> {
    return withContext(Dispatchers.Default) {
        val text = get("jsxsd/xsxkjg/comeXkjglb").bodyAsText()

        val doc = Ksoup.parse(text)
        val tds = doc.getElementsByTag("td")

        val courses = mutableListOf<MCourse>()
        for (index in tds.indices step 9) {
            courses.add(
                MCourse(
                    courseId = tds[index].text(),
                    name = tds[index + 1].text(),
                    credits = tds[index + 2].text().toDouble(),
                    category = tds[index + 3].text(),
                    teacher = tds[index + 4].text(),
                    time = tds[index + 5].text().takeIf { it.isNotEmpty() },
                    classroom = tds[index + 6].text().takeIf { it.isNotEmpty() },
                    id = tds[index + 8].child(0).attr("href").substring(21, 36),
                )
            )
        }
        return@withContext courses
    }
}