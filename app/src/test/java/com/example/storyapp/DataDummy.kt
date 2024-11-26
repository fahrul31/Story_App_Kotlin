package com.example.storyapp

import com.example.storyapp.remote.response.Story

object DataDummy {

    fun generateDummyStories(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..10) {
            val story = Story(
                id = "story-HsG1aRRRWtLSfSSi",
                name = "test",
                description =  "test",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1732358914211_c20af48b5031630e4b05.20241123_1748188386166841092819459jpg",
                createdAt =  "2024-11-23T10:48:34.221Z",
                lat =  1.0,
                lon = 1.0
            )
            items.add(story)
        }
        return items
    }
}