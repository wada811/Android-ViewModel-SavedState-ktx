package com.wada811.viewmodelsavedstate.sample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.wada811.viewmodelsavedstate.SavedStateAdapter
import com.wada811.viewmodelsavedstate.liveData

class SampleViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    enum class CountUpValue(val count: Int) {
        ONE(1),
        TEN(10)
    }

    private var viewModelCount = MutableLiveData(0)
    var viewModelCountText: LiveData<String> = MediatorLiveData<String>().also { liveData ->
        liveData.addSource(viewModelCount) { count ->
            liveData.value = "$count"
        }
    }
    val countUpValueEnumLiveData: MutableLiveData<CountUpValue?> by savedStateHandle.liveData(object : SavedStateAdapter<CountUpValue?, Int?> {
        override fun toSavedState(value: CountUpValue?): Int? = value?.ordinal
        override fun fromSavedState(state: Int?): CountUpValue? = CountUpValue.values().firstOrNull { it.ordinal == state }
    })
    val savedStateCount: MutableLiveData<Int> by savedStateHandle.liveData(0)
    var savedStateCountText: LiveData<String> = MediatorLiveData<String>().also { liveData ->
        liveData.addSource(savedStateCount) { count ->
            liveData.value = "$count"
        }
    }

    val log: MutableLiveData<String> by savedStateHandle.liveData("Log:")

    init {
        appendLog("ViewModel::init")
    }

    fun countUp() {
        viewModelCount.value = viewModelCount.value?.plus(countUpValueEnumLiveData.value?.count ?: 0)
        savedStateCount.value = savedStateCount.value?.plus(countUpValueEnumLiveData.value?.count ?: 0)
    }

    fun appendLog(text: String) {
        val maxLineCount = 5
        val logLines = log.value!!.split("\n")
        if (logLines.size > maxLineCount) {
            log.value = logLines.subList(logLines.size - maxLineCount + 1, logLines.size).joinToString("\n")
        }
        log.value = log.value + "\n$text"
    }

    override fun onCleared() {
        super.onCleared()
        appendLog("ViewModel::onCleared")
    }
}