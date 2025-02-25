package org.kiteio.punica.ui.page.timetable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.foundation.ICourse
import org.kiteio.punica.ui.page.home.TopLevelRoute
import org.kiteio.punica.ui.rememberRBlocking
import org.kiteio.punica.ui.widget.LoadingNotNullOrEmpty
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.timetable

/**
 * 课表页面路由。
 */
@Serializable
object TimetableRoute : TopLevelRoute {
    override val nameRes = Res.string.timetable
    override val icon = Icons.Outlined.DateRange
    override val toggledIcon = Icons.Filled.DateRange
}

/**
 * 课表页面。
 */
@Composable
fun TimetablePage() = viewModel { TimetableVM() }.Content()


@Composable
private fun TimetableVM.Content() {
    val scope = rememberCoroutineScope()
    val week = rememberRBlocking { AppVM.week.first() }
    val state = rememberPagerState(initialPage = week) { AppVM.TIMETABLE_MAX_PAGE }

    // 备注对话框可见性
    var noteDialogVisible by remember { mutableStateOf(false) }

    // 课程对话框可见性
    var coursesDialogVisible by remember { mutableStateOf(false) }
    var visibleCourses by remember { mutableStateOf<List<ICourse>?>(null) }

    // 监听账号和学期变化，切换课表
    LaunchedEffect(AppVM.academicSystem, term) {
        scope.launchCatching { switchTimetable() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                week = week,
                currentPage = state.currentPage,
                onPageChange = { scope.launchCatching { state.scrollToPage(it) } },
                onNoteDialogDisplayRequest = { noteDialogVisible = true },
            )
        },
        contentWindowInsets = WindowInsets.statusBars,
    ) { innerPadding ->
        LoadingNotNullOrEmpty(
            timetable,
            isLoading = isTimetableLoading,
            modifier = Modifier.padding(innerPadding),
        ) { timetable ->
            val spacing = 2.dp
            val lineHeight = 600.dp
            val timelineWeight = 0.05f
            val timelineMinWidth = 32.dp

            Column {
                // 星期
                TimetableHeader(
                    week = week,
                    currentPage = state.currentPage,
                    spacing = spacing,
                    timelineWeight = timelineWeight,
                    timelineMinWidth = timelineMinWidth,
                    modifier = Modifier.padding(spacing),
                )
                // 时间线和表格
                TimetablePager(
                    state = state,
                    courses = timetable.cells,
                    onItemClick = {
                        visibleCourses = timetable.cells[it]
                        coursesDialogVisible = true
                    },
                    spacing = spacing,
                    lineHeight = lineHeight,
                    timelineWeight = timelineWeight,
                    timelineMinWidth = timelineMinWidth,
                    modifier = Modifier.weight(1f),
                )
                // 课表备注
                if (isBottomNoteVisible && timetable.note != null) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        Text(
                            timetable.note,
                            modifier = Modifier.padding(spacing),
                            color = LocalContentColor.current.copy(0.6f),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }

    // 备注对话框
    NoteDialog(
        noteDialogVisible,
        onDismissRequest = { noteDialogVisible = false },
        note = timetable?.note,
        bottomNoteVisible = isBottomNoteVisible,
        onBottomNoteVisibleChange = ::switchBottomNoteVisible,
    )

    CoursesDialog(
        coursesDialogVisible,
        onDismissRequest = {
            coursesDialogVisible = false
            visibleCourses = null
        },
        courses = visibleCourses,
    )
}