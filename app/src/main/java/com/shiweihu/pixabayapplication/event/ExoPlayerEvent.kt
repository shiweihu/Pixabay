package com.shiweihu.pixabayapplication.event

import com.google.android.exoplayer2.ExoPlayer
import com.jeremyliao.liveeventbus.core.LiveEvent

class ExoPlayerEvent(val player:ExoPlayer):LiveEvent {
}