package com.app.smartscan.testutil

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

fun runBlockingUnit(block: suspend CoroutineScope.() -> Unit) =
    runBlocking(Dispatchers.IO) { block() }