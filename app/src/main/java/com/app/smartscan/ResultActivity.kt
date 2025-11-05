package com.app.smartscan

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Block
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class ResultActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Ensure Firebase is initialized (fixes "not logged in" issue)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "You must be logged in to save products.", Toast.LENGTH_LONG).show()
        }

        val productName = intent.getStringExtra("PRODUCT_NAME") ?: "Unknown Product"
        val productNote = intent.getStringExtra("PRODUCT_NOTE") ?: "Product information not found."

        setContent {
            MaterialTheme {
                ResultScreen(
                    productName = productName,
                    productNote = productNote,
                    onBack = { finish() },
                    onAddFavorite = {
                        if (auth.currentUser != null) {
                            Toast.makeText(this, "Added to Favorites 💜", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Please log in first.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onAddBlacklist = {
                        if (auth.currentUser != null) {
                            Toast.makeText(this, "Added to Blacklist 🚫", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Please log in first.", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ResultScreen(
    productName: String,
    productNote: String,
    onBack: () -> Unit,
    onAddFavorite: () -> Unit,
    onAddBlacklist: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = productName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = productNote,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(onClick = onAddFavorite) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Add to Favorites",
                            tint = Color(0xFFDB3A76),
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(36.dp))

                    IconButton(onClick = onAddBlacklist) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = "Add to Blacklist",
                            tint = Color(0xFFCC2F2F),
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .width(120.dp)
                        .height(45.dp)
                ) {
                    Text("Back")
                }
            }
        }
    }
}
