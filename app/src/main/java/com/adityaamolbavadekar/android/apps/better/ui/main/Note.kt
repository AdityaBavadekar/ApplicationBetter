package com.adityaamolbavadekar.android.apps.better.ui.main

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adityaamolbavadekar.android.apps.better.utils.Constants
import java.util.ConcurrentModificationException
/*
@Entity(tableName = "better_notes" )
class Note(
    @PrimaryKey(autoGenerate = true)
    var id : Long,
    @ColumnInfo(name = "TITLE")
    var title:String,
    @ColumnInfo(name = "BODY")
    var body:String,
    @ColumnInfo(name = "CREATED_ON")
    var createdOn:String,
    @ColumnInfo(name = "MODIFIED_ON")
    var modificationDate:String,
    @ColumnInfo(name = "LABELS")
    var labels :List<String>,
    @ColumnInfo(name = "IMAGE_PATH")
    var imagePath :String,
    @ColumnInfo(name = "LINKS")
    var links :List<String>,
    @ColumnInfo(name = "HAS_IMAGE")
    var hasImage:Boolean,
    @ColumnInfo(name = "HAS_LABELS")
    var hasLabels:Boolean,
    @ColumnInfo(name = "HAS_LINK_PREVIEWS")
    var hasLinksPreviews:Boolean
){
}*/

class Note(
    var id : Long,
    var title:String,
    var body:String,
    var createdOn:String,
    var modificationDate:String,
    var labels :ArrayList<String>,
    var imagePath :String,
    var links :ArrayList<String>,
    var hasImage:Boolean,
    var hasLabels:Boolean,
    var hasLinksPreviews:Boolean
){
}
