package com.mohil_bansal.repo_quest.core

interface Callback<T> {
    fun onSuccess(data: T)
    fun onFailure(msg: String?)
    fun onError(mdg: String?)
    fun onComplete()
}