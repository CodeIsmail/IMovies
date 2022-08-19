package dev.codeismail.imovies.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey
    val id: String,
    val title: String,
    val posterUrl: String,
    val voteAverage: Double)
