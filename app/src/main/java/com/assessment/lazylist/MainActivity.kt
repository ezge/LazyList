package com.assessment.lazylist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.assessment.lazylist.ui.theme.LazyListTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.*
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility

class MainActivity : ComponentActivity() {
    private var itemArray: Array<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        itemArray = resources.getStringArray(R.array.car_array)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LazyListTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(itemArray = itemArray as Array<out String>,
                               modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen( itemArray: Array<out String>, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    /*This is used when a state is created as the result of a calculation,
      mainly when a state included in the calculation is subject
      to changes outside the current composable. In this case, we are calculating
      whether the firstVisibleIndex value of the LazyColumn composable is greater than 5.
      When derivedStateOf is used, a cached version of the state is returned,
      and the state is only recalculated when the listState value has changed.
      This helps to prevent repeated calculations from being performed
      unnecessarily and degrading app performance.*/
    val displayButton = remember {derivedStateOf{ listState.firstVisibleItemIndex > 5 }}
    val context = LocalContext.current
    val groupedItems = itemArray.groupBy { it.substringBefore(' ') }
    val onListItemClick = { text : String ->
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    Box(modifier) {
        LazyColumn(state = listState,
                   contentPadding = PaddingValues(50.dp)) {
            groupedItems.forEach { (manufacturer, models) ->
                stickyHeader {
                    Text(
                        text = manufacturer,
                        color = Color.White,
                        modifier = Modifier
                            .background(Color.Gray)
                            .padding(5.dp)
                            .fillMaxWidth()
                    )
                }
                items(models) { model ->
                    MyListItem(item = model, onItemClick = onListItemClick)
                }
            }
        }
        /*AnimatedVisibility composable controls the position and
          visibility of the button so that it appears at the bottom
          center of the screen and is only visible when displayButton
          is true. it animates the hiding and showing of its child components. */
        AnimatedVisibility(visible = displayButton.value,
                           Modifier.align(Alignment.BottomCenter)) {
            OutlinedButton(
                onClick = { coroutineScope.launch { listState.scrollToItem(0) } },
                border = BorderStroke(1.dp, Color.Gray),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray),
                modifier = Modifier.padding(5.dp)
            ) {
                Text(text = "Top")
            }
        }
    }
}

@Composable
fun MyListItem(item: String, onItemClick: (String) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
         modifier = Modifier.padding(8.dp)
                            .fillMaxWidth()
                            .clickable { onItemClick(item) },
         elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)){
        Row(verticalAlignment = Alignment.CenterVertically) {
            ImageLoader(item)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = item,
                 style = MaterialTheme.typography.headlineSmall,
                 modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun ImageLoader(item: String) {
    val url = "https://www.ebookfrenzy.com/book_examples/car_logos/" +
            item.substringBefore(" ") + "_logo.png"

    Image(
        painter = rememberAsyncImagePainter(url),
        contentDescription = "car image",
        contentScale = ContentScale.Fit,
        modifier = Modifier.size(75.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val itemArray: Array<String> = arrayOf("Cadillac Eldorado", "Ford Fairlane", "Plymouth Fury")
    LazyListTheme {
        MainScreen( itemArray = itemArray )
    }
}