package hu.ait.traveldiary.ui.screen.add

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import hu.ait.traveldiary.R
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddEntryScreen(
    addEntryViewModel: AddEntryViewModel = viewModel()
) {
    var postTitle by remember { mutableStateOf("") }
    var postBody by remember { mutableStateOf("") }
    var startDate by remember {
        mutableStateOf(LocalDate.now())
    }
    var endDate by remember {
        mutableStateOf(LocalDate.now())
    }

    val formattedStartDate by remember {
        derivedStateOf{
            DateTimeFormatter
                .ofPattern("MM dd yyyy")
                .format(startDate)
        }
    }

    val formattedEndDate by remember {
        derivedStateOf{
            DateTimeFormatter
                .ofPattern("MM dd yyyy")
                .format(endDate)
        }
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val context = LocalContext.current

    val bitmap = remember {mutableStateOf<Bitmap?>(null)}

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }


    var hasImage by remember {
        mutableStateOf(false)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage = success
        }
    )
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )



    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        imageUri?.let {
            val source = ImageDecoder.createSource(context.contentResolver, it)
            bitmap.value = ImageDecoder.decodeBitmap(source)

            bitmap.value?.let { btm ->
                Image(
                    bitmap = btm.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(400.dp)
                        .padding(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { launcher.launch("image/*")}) {
            Text(text = "upload an image")
        }


        OutlinedTextField(value = postTitle,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Post title") },
            onValueChange = {
                postTitle = it
            }
        )
        OutlinedTextField(value = postBody,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Post body") },
            onValueChange = {
                postBody = it
            }
        )

        // permission here...
        if (cameraPermissionState.status.isGranted) {
            Button(onClick = {
                val uri = ComposeFileProvider.getImageUri(context)
                imageUri = uri
                cameraLauncher.launch(uri) // opens the built in camera
            }) {
                Text(text = "Take photo")
            }
        } else {
            Column() {
                val permissionText = if (cameraPermissionState.status.shouldShowRationale) {
                    "Please reconsider giving the camera persmission it is needed if you want to take photo for the message"
                } else {
                    "Give permission for using photos with items"
                }
                Text(text = permissionText)
                Button(onClick = {
                    cameraPermissionState.launchPermissionRequest()
                }) {
                    Text(text = "Request permission")
                }
            }
        }

        if (hasImage && imageUri != null) {
            AsyncImage(model = imageUri,
                modifier = Modifier.size(200.dp, 200.dp),
                contentDescription = "selected image")
        }

        Button(onClick = {
            if (imageUri == null) {
                addEntryViewModel.uploadPost(postTitle, postBody)
            } else {
                addEntryViewModel
                    .uploadPostImage(
                        context.contentResolver,
                        imageUri!!,
                        postTitle,
                        postBody
                    )
            }
        }) {
            Text(text = "Upload")
        }

        when (addEntryViewModel.writePostUiState) {
            is WritePostUiState.LoadingPostUpload -> CircularProgressIndicator()
            is WritePostUiState.PostUploadSuccess -> {
                Text(text = "Post uploaded.")
            }
            is WritePostUiState.ErrorDuringPostUpload ->
                Text(text =
                "${(addEntryViewModel.writePostUiState as WritePostUiState.ErrorDuringPostUpload).error}")

            is WritePostUiState.LoadingImageUpload -> CircularProgressIndicator()
            is WritePostUiState.ImageUploadSuccess -> {
                Text(text = "Image uploaded, starting post upload.")
            }
            is WritePostUiState.ErrorDuringImageUpload ->
                Text(text = "${(addEntryViewModel.writePostUiState as WritePostUiState.ErrorDuringImageUpload).error}")


            else -> {}
        }
    }
}

class ComposeFileProvider : FileProvider(
    R.xml.filepaths
) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory,
            )
            val authority = context.packageName + ".fileprovider"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }
}