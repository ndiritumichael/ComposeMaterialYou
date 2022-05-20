/*
 * Copyright (c) 2022 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.app.composematerialyou.presentation.screens

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.app.composematerialyou.data.models.UserEvent
import com.app.composematerialyou.presentation.composables.EmptyStateScreen
import com.app.composematerialyou.presentation.composables.SwipableCalendarItem
import com.app.composematerialyou.presentation.composables.TopBar
import com.app.composematerialyou.presentation.navigation.AppNavigation
import com.app.composematerialyou.presentation.navigation.Screens
import com.app.composematerialyou.presentation.theme.ComposeMaterialYou
import com.app.composematerialyou.presentation.viewmodels.EventsViewModel
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.android.ext.android.inject

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
class HomeActivityScreen : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    WindowCompat.setDecorFitsSystemWindows(window, false)
    setContent {

      val navController = rememberAnimatedNavController()
      val eventsViewModel: EventsViewModel by inject()
      ComposeMaterialYou {

        Box(modifier = Modifier.systemBarsPadding()){
          AppNavigation(navController, eventsViewModel)
        }


      }
    }
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(navController: NavController, eventsViewModel: EventsViewModel) {
  val keyboardController = LocalSoftwareKeyboardController.current
  keyboardController?.hide()
  val showCompleted by eventsViewModel.showCompleted.collectAsState()
  val events by eventsViewModel.events.collectAsState()
  val systemUiController = rememberSystemUiController()
  SetSystemUiColors(systemUiController = systemUiController,
    MaterialTheme.colorScheme.onPrimary)


LaunchedEffect(key1 = true, block = {
  eventsViewModel.resetValues()
})
  Scaffold(
    modifier = Modifier
      .fillMaxSize(),
    topBar = { TopBar(if (showCompleted) "Completed Events" else " Pending Events"){

      Switch(checked = showCompleted, onCheckedChange = {eventsViewModel.toggleCompleted()})
     /* IconToggleButton(checked =showCompleted , onCheckedChange = {eventsViewModel.toggleCompleted()}) {
        val tint by animateColorAsState(if (showCompleted) Color(0xFFEC407A) else Color(0xFFB0BEC5))
        Icon(Icons.Filled.Favorite, contentDescription = "Localized description", tint = tint)


      }*/
    } },
    content = {

      if (events.isEmpty()) {
        EmptyStateScreen()
      } else {
        events.let { userEvents ->
          Box(Modifier.padding(it)) {
                EventList(events = userEvents, editEvent = {
                  eventsViewModel.updateDetails(it)
                  navController.navigate(Screens.EventInputScreen.route)}){
                  eventsViewModel.markAsDone(it)
                }
          }
        }
      }

    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        modifier = Modifier
          .padding(16.dp),
        onClick = {
          navController.navigate(Screens.EventInputScreen.route)
        },
        icon = {
          Icon(
            Icons.Filled.Add,
            contentDescription = "Create"
          )
        },
        text = { Text("Create") }
      )
    }
  )
}

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
fun EventList(events: List<UserEvent>,editEvent:(UserEvent) -> Unit,markAsDone : (UserEvent)-> Unit) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.onPrimary)
  ) {
    items(events) { userEvent ->
      SwipableCalendarItem(modifier = Modifier.animateItemPlacement(), userEvent = userEvent,editEvent = {editEvent(userEvent)}, markAsDone = markAsDone)
      //CalendarListItem(userEvent)
    }
  }
}

@Composable
fun SetSystemUiColors(systemUiController: SystemUiController,color: Color){

  val darkIcons = isSystemInDarkTheme()
  SideEffect {
    systemUiController.setSystemBarsColor(color = color,darkIcons.not())
  }
}
