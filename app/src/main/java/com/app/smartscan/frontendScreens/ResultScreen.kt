package com.app.smartscan.frontendScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.smartscan.R
import com.app.smartscan.data.model.Product
import com.app.smartscan.data.model.Scan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun ResultScreen(
    navController: NavController,
    scanId: String?,
    observeScan: (String) -> Flow<Scan?>,
    getProduct: suspend (String) -> Product?,
    isGuest: Boolean,
    onAddToFavorites: (String) -> Unit,
    onAddToBlacklist: (String) -> Unit,
    onBack: () -> Unit
) {
    val scan by if (scanId != null) {
        observeScan(scanId).collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }

    var product by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(scan) {
        scan?.productKey?.let {
            product = getProduct(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("⬅ Back", color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(Modifier.height(8.dp))

        Image(
            painter = painterResource(id = R.drawable.smartskin_logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(140.dp)
        )

        Spacer(Modifier.height(8.dp))

        if (scan == null || (scan?.productKey != null && product == null)) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text(text = if (scan == null) "Generating explanation..." else "Fetching product details...")
        } else {
            if (!isGuest && product?.barcode != null) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { product?.barcode?.let { onAddToBlacklist(it) } }) {
                        Icon(painter = painterResource(R.drawable.ic_broken_heart), contentDescription = "Blacklist", tint = Color.Red)
                    }
                    IconButton(onClick = { product?.barcode?.let { onAddToFavorites(it) } }) {
                        Icon(painter = painterResource(R.drawable.ic_heart), contentDescription = "Favorite", tint = Color.Red)
                    }
                }
            }

            OutlinedTextField(
                value = product?.name ?: "N/A",
                onValueChange = {},
                readOnly = true,
                label = { Text("Product") },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )

            OutlinedTextField(
                value = product?.category ?: "N/A",
                onValueChange = {},
                readOnly = true,
                label = { Text("Type") },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )

            val explanationText = scan?.explanation?.get("summary") as? String ?: "Loading..."
            OutlinedTextField(
                value = explanationText,
                onValueChange = {},
                readOnly = true,
                label = { Text("Skin Compatibility") },
                modifier = Modifier.fillMaxWidth().height(130.dp).padding(top = 12.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "⚠️ AI-generated result — may contain errors.\nConsult a professional if unsure.",
                color = Color.Red,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true, name = "Result Screen Loading")
@Composable
fun ResultScreenLoadingPreview() {
    MaterialTheme {
        ResultScreen(
            navController = rememberNavController(),
            scanId = "preview123",
            observeScan = { flowOf(null) }, // Simulate loading scan
            getProduct = { null },
            isGuest = true,
            onAddToFavorites = {},
            onAddToBlacklist = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Result Screen Registered")
@Composable
fun ResultScreenRegisteredPreview() {
    val previewScan = Scan(
        productKey = "12345",
        explanation = mapOf("summary" to "This is a preview explanation.")
    )
    val previewProduct = Product(
        name = "Super Glow Serum",
        category = "Serum",
        barcode = "12345"
    )
    MaterialTheme {
        ResultScreen(
            navController = rememberNavController(),
            scanId = "preview123",
            observeScan = { flowOf(previewScan) },
            getProduct = { previewProduct },
            isGuest = false,
            onAddToFavorites = {},
            onAddToBlacklist = {},
            onBack = {}
        )
    }
}
