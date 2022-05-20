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
package com.app.composematerialyou.presentation.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.composematerialyou.data.models.EventColors
import com.app.composematerialyou.data.models.UserEvent
import com.app.composematerialyou.data.repository.EventsRepository
import com.app.composematerialyou.utils.stringToColor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EventsViewModel(private val eventsRepository: EventsRepository) : ViewModel() {


  private val _userEvent = MutableStateFlow<List<UserEvent>>(emptyList())

  val showCompleted = MutableStateFlow(false)
  @OptIn(ExperimentalCoroutinesApi::class)
  val events = showCompleted.flatMapLatest {
    eventsRepository.getAllEvents(it)
  }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
  var userSelectedColor = EventColors(Color.Green, "Default Color")
  var eventName = ""
  var eventDescription = ""
  var date = MutableStateFlow("")
  var id= 0

  fun addEvent() {
    viewModelScope.launch {
      eventsRepository.addEvent(
        UserEvent(
          id,
          eventName,
          eventDescription,
          userSelectedColor.name,
          date.value
        )
      )

      resetValues()
    }
  }

 fun resetValues() {
    userSelectedColor = EventColors(Color.Green, "Default Color")
   eventName = ""
   eventDescription = ""
  date.value = ""
    id = 0
  }

  fun getAllEvents(completed:Boolean) {
    viewModelScope.launch {
      eventsRepository.getAllEvents(completed = completed).collect {
        _userEvent.value = it
      }
    }
  }

  fun validate(): Boolean =
    eventName.isNotEmpty() && eventDescription.isNotEmpty() && date.value.isNotEmpty()

  fun deleteEvent(event: UserEvent){

    viewModelScope.launch {
      eventsRepository.deleteEvent(event)
    }
  }

  fun markAsDone(userEvent: UserEvent){
    viewModelScope.launch {  eventsRepository.addEvent(userEvent) }
  }
  fun toggleCompleted(){
    showCompleted.value = showCompleted.value.not()
  }

  fun updateDetails(userEvent: UserEvent){
    userSelectedColor = EventColors(userEvent.eventColor.stringToColor(),userEvent.eventColor)
    eventName = userEvent.name
    eventDescription = userEvent.description
    date.value = userEvent.date
    id = userEvent.id
  }

}