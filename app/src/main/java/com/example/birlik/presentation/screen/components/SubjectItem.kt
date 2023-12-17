package com.example.birlik.presentation.screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.birlik.R

@Composable
fun SubjectItem(
    title: String,
    subjectImage: Int,
    countryImage: Int,
    lastMessage: String,
    time: String,
    showFlag: Boolean = false
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(30.dp)){
            Image(modifier = Modifier.size(30.dp),painter = painterResource(id = subjectImage), contentDescription = "")
            if (showFlag){
                Image(modifier = Modifier.size(15.dp).align(Alignment.TopStart),painter = painterResource(id = countryImage), contentDescription = "")
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        Column() {
            Text(text = title, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = lastMessage, color = Color.Gray)
                Text(text = time, color = Color.Gray)
            }
        }
    }
}