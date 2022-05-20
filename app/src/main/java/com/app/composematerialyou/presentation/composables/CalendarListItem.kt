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
package com.app.composematerialyou.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.app.composematerialyou.data.models.UserEvent
import com.app.composematerialyou.utils.stringToColor

@ExperimentalMaterial3Api
@Composable
fun CalendarListItem(userEvent: UserEvent,editEvent: () -> Unit) {
  var showDescription by remember { mutableStateOf(false) }



  ElevatedCard(
    modifier = Modifier
      .fillMaxWidth()
      .padding(6.dp),
    colors = CardDefaults.cardColors(
      containerColor = userEvent.eventColor.stringToColor()

    ),
    shape = RoundedCornerShape(15.dp),
    onClick = editEvent
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
    ) {
      ConstraintLayout(
        modifier = Modifier
          .fillMaxWidth()
      ) {
        val (tvName, icon) = createRefs()
        Text(
          text = userEvent.name,
          modifier = Modifier
            .constrainAs(tvName) {
              top.linkTo(parent.top)
              bottom.linkTo(parent.bottom)
              start.linkTo(parent.start)
            },
          color = Color.White,
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold
        )
        Icon(
          imageVector = if (showDescription) Icons.Default.KeyboardArrowUp else
            Icons.Default.KeyboardArrowDown,
          contentDescription = null,
          modifier = Modifier
            .size(30.dp, 30.dp)
            .clickable(
              indication = rememberRipple(bounded = true),
              interactionSource = remember {
                MutableInteractionSource()
              }
            ) {
              showDescription = !showDescription
            }
            .constrainAs(icon) {
              top.linkTo(parent.top)
              bottom.linkTo(parent.bottom)
              end.linkTo(parent.end)
            },
          tint = Color.White,
        )
      }
      Spacer(
        modifier = Modifier
          .padding(top = 10.dp)
      )

      AnimatedVisibility(visible = showDescription) {
        Text(
          text = userEvent.description,
          color = Color.White
        )
        Spacer(
          modifier = Modifier
            .padding(top = 10.dp)
        )
      }


      Text(
        text = userEvent.date,
        color = Color.White
      )
    }
  }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SwipableCalendarItem(
  modifier: Modifier = Modifier,
  userEvent: UserEvent,
  markAsDone: (UserEvent) -> Unit,
  editEvent: () -> Unit
){

  val dismissState = rememberDismissState(initialValue = DismissValue.Default,
  confirmStateChange = {
    if (it == DismissValue.DismissedToStart){markAsDone(userEvent.copy(completed = true))}
    it == DismissValue.DismissedToEnd

  })
  SwipeToDismiss(
    modifier = modifier,
    state = dismissState,
    background = {
      val color by animateColorAsState(
        when (dismissState.targetValue) {
          DismissValue.Default -> Color.Transparent
          DismissValue.DismissedToEnd -> Color.Green
          DismissValue.DismissedToStart -> Color.Red
        }
      )
      val direction = dismissState.dismissDirection

      if (direction == DismissDirection.StartToEnd) {

        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(8.dp)
        ) {
          Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(
              imageVector = Icons.Default.ArrowForward,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
              text = "Move to Archive", fontWeight = FontWeight.Bold,
              textAlign = TextAlign.Center,
              color = Color.White
            )
          }

        }
      } else {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(8.dp)
        ) {
          Column(modifier = Modifier.align(Alignment.CenterEnd)) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.heightIn(5.dp))
            Text(
              text = "Move to Bin",
              textAlign = TextAlign.Center,
              fontWeight = FontWeight.Bold,
              color = Color.LightGray
            )

          }
        }
      }
    },
    dismissContent = {CalendarListItem(userEvent = userEvent){editEvent()} },
    directions = setOf(DismissDirection.EndToStart,DismissDirection.StartToEnd),
    dismissThresholds = {
      androidx.compose.material.FractionalThreshold(0.4f)
    },

    )

    }

/*
@Composable
fun SwipeToReveal(userEvent: UserEvent){
  Swipe

}*/
