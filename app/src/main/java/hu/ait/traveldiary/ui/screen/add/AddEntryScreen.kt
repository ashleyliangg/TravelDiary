package hu.ait.traveldiary.ui.screen.add

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import hu.ait.traveldiary.R
import hu.ait.traveldiary.utils.bounceClick
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddEntryScreen(
    addEntryViewModel: AddEntryViewModel = viewModel()
) {
    var postTitle by remember { mutableStateOf("") }
    var postBody by remember { mutableStateOf("") }
    var postLocation by remember { mutableStateOf("") }
    var startDate by remember {
        mutableStateOf(LocalDate.now())
    }
//    var endDate by remember {
//        mutableStateOf(LocalDate.now())
//    }

    val formattedStartDate by remember {
        derivedStateOf {
            DateTimeFormatter.ofPattern("MM/dd/yyyy").format(startDate)
        }
    }

//    val formattedEndDate by remember {
//        derivedStateOf{
//            DateTimeFormatter
//                .ofPattern("MM/dd/yyyy")
//                .format(endDate)
//        }
//    }

    val context = LocalContext.current

    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var hasImage by remember {
        mutableStateOf(false)
    }

    val photoAlbumLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),
            onResult = { uri: Uri? ->
                imageUri = uri
                hasImage = true
            })

    val startDateDialogState = rememberMaterialDialogState()
    val endDateDialogState = rememberMaterialDialogState()


    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )
    Scaffold(topBar = {
        TopAppBar(title = { Text("Add an entry") }, colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ), actions = {
//            IconButton(onClick = { }) {
//                Icon(Icons.Filled.Info, contentDescription = "Info")
//            }
        })
    }


    ) {
        it
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 65.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (!hasImage) {
                        val imageUploadComposition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes(
                                R.raw.image_upload
                            )
                        )
                        val imageUploadProgress by animateLottieCompositionAsState(
                            imageUploadComposition,
                            iterations = LottieConstants.IterateForever,
                            isPlaying = true
                        )
                        LottieAnimation(
                            composition = imageUploadComposition,
                            progress = imageUploadProgress,
                            modifier = Modifier
                                .size(100.dp)
                        )
                    }
                    Row {
                        //val imageUploadComposition by rememberLo
                        //display image
                        imageUri?.let {
                            val source = ImageDecoder.createSource(context.contentResolver, it)
                            bitmap.value = ImageDecoder.decodeBitmap(source)

                            bitmap.value?.let { btm ->
                                Image(
                                    bitmap = btm.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(200.dp)
                                        .padding(20.dp)
                                )
                            }
                        }
                    }
                    Row {
                        // permission here...
                        if (cameraPermissionState.status.isGranted) {
                            Button(
                                onClick = {
                                    photoAlbumLauncher.launch("image/*")
                                }) {
                                if (hasImage) {
                                    Text(text = "Change photo")
                                } else {
                                    Text(text = "Upload a photo")
                                }
                            }

                            if (hasImage) {
                                Spacer(modifier = Modifier.padding(10.dp))
                                Button(
                                    onClick = {
                                        imageUri = null
                                        hasImage = false
                                    }) {
                                    Text(text = "Delete photo")
                                }
                            }
                        } else {
                            Column() {
                                val permissionText =
                                    if (cameraPermissionState.status.shouldShowRationale) {
                                        "Please reconsider giving the camera permission it is needed if you want to take photo for the message"
                                    } else {
                                        "Give permission for using photos with items"
                                    }
                                Text(text = permissionText)
                                Button(modifier = Modifier.bounceClick(),
                                    onClick = {
                                        cameraPermissionState.launchPermissionRequest()
                                    }) {
                                    Text(text = "Request permission")
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.padding(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            startDateDialogState.show()
                        }) {
                        Text(text = "Select date")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(text = formattedStartDate)
                }

                Spacer(modifier = Modifier.width(10.dp))

//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Button(onClick = {
//                        endDateDialogState.show()
//                    }) {
//                        Text(text = "Select end date")
//                    }
//
//                    Spacer(modifier = Modifier.width(10.dp))
//
//                    Text(text = formattedEndDate)
//                }

                MaterialDialog(dialogState = startDateDialogState, buttons = {
                    positiveButton(text = "Ok") {

                    }
                    negativeButton(text = "Cancel") {

                    }
                }) {
                    datepicker(initialDate = LocalDate.now(), title = "Pick a date",
                        //colors = DatePickerDefaults.colors(Color.Magenta),
                        allowedDateValidator = {
                            it.isBefore(LocalDate.now())
                        }) {
                        startDate = it
                    }
                }

//                MaterialDialog(
//                    dialogState = endDateDialogState,
//                    buttons = {
//                        positiveButton(text = "Ok") {
//
//                        }
//                        negativeButton(text = "Cancel") {
//
//                        }
//                    }
//                ) {
//                    datepicker(
//                        initialDate = LocalDate.now(),
//                        title = "Pick a date",
//                        //colors = DatePickerDefaults.colors(Color.Magenta),
//                        allowedDateValidator = {
//                            it.isAfter(startDate) || it == startDate
//                        }
//                    ) {
//                        endDate = it
//                    }
//                }
                Spacer(Modifier.padding(5.dp))

                OutlinedTextField(value = postLocation,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            text = "Trip Location..",
                            fontWeight = FontWeight.Light,
                            fontSize = 12.sp
                        )
                    },
                    onValueChange = {
                        postLocation = it
                    })

                OutlinedTextField(value = postTitle, modifier = Modifier.fillMaxWidth(), label = {
                    Text(
                        text = "Trip name..", fontWeight = FontWeight.Light,
                        fontSize = 12.sp
                    )
                }, onValueChange = {
                    postTitle = it
                })
                OutlinedTextField(value = postBody, modifier = Modifier.fillMaxWidth(), label = {
                    Text(
                        text = "Trip details: share what you did!",
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp
                    )
                }, onValueChange = {
                    postBody = it
                })


//                if (hasImage && imageUri != null) {
//                    AsyncImage(
//                        model = imageUri,
//                        modifier = Modifier.size(200.dp, 200.dp),
//                        contentDescription = "selected image"
//                    )
//                }
                Spacer(Modifier.padding(15.dp))

                Button(onClick = {
                    if (imageUri == null) {
                        addEntryViewModel.uploadPost(
                            postLocation,
                            postTitle,
                            postBody,
                            formattedStartDate,
//                            formattedEndDate,
                            //toString = task.result.toString()
                        )
                    } else {
                        addEntryViewModel.uploadPostImage(
                            context.contentResolver,
                            imageUri!!,
                            postLocation,
                            postTitle,
                            postBody,
                            formattedStartDate,
//                                formattedEndDate
                        )
                    }
                    postTitle = ""
                    postBody = ""
                    postLocation = ""
                    startDate = LocalDate.now()
                }) {
                    Text(text = "Create Post")
                }


                when (addEntryViewModel.writePostUiState) {
                    is WritePostUiState.LoadingPostUpload -> CircularProgressIndicator()
                    is WritePostUiState.PostUploadSuccess -> {
                        val uploadedComposition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes(
                                R.raw.uploaded
                            )
                        )
                        val uploadedProgress by animateLottieCompositionAsState(
                            uploadedComposition,
                            iterations = LottieConstants.IterateForever,
                            isPlaying = true
                        )
                        LottieAnimation(
                            composition = uploadedComposition,
                            progress = uploadedProgress,
                            modifier = Modifier
                                .size(100.dp)
                        )
                    }

                    is WritePostUiState.ErrorDuringPostUpload -> Text(
                        text = "${(addEntryViewModel.writePostUiState as WritePostUiState.ErrorDuringPostUpload).error}"
                    )

                    is WritePostUiState.LoadingImageUpload -> CircularProgressIndicator()
                    is WritePostUiState.ImageUploadSuccess -> {
                        Text(text = "Image uploaded, starting post upload.")
                    }

                    is WritePostUiState.ErrorDuringImageUpload -> Text(text = "${(addEntryViewModel.writePostUiState as WritePostUiState.ErrorDuringImageUpload).error}")

                    else -> {}
                }
            }

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

