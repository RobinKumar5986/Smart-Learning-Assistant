package com.nationalhackaton.smartlearningassist.activititys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.nationalhackaton.smartlearningassist.BuildConfig;
import com.nationalhackaton.smartlearningassist.R;
import com.nationalhackaton.smartlearningassist.adapters.ChatAdapter;
import com.nationalhackaton.smartlearningassist.adapters.ImageAdapter;
import com.nationalhackaton.smartlearningassist.databinding.ActivityAiChatFragmentBinding;
import com.nationalhackaton.smartlearningassist.models.MessageModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.speech.tts.UtteranceProgressListener;


public class AiChatFragment extends AppCompatActivity implements View.OnClickListener,TextToSpeech.OnInitListener {
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    ActivityAiChatFragmentBinding binding;
    ArrayList<MessageModel> messageModels=new ArrayList<>();
    List<Bitmap> imageList=new ArrayList<>();
    private ActivityResultLauncher<Intent> cameraLauncher;
    ImageAdapter adapter=new ImageAdapter(imageList);
    ChatAdapter chatAdapter=new ChatAdapter(messageModels,this);
    Content content;


    private TextToSpeech textToSpeech;
    String ansByAi="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAiChatFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getExtras() != null) {
                            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                            SetImages(imageBitmap);
                        }
                    }
                }
        );

        textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // Speech started
            }

            @Override
            public void onDone(String utteranceId) {
                // Speech completed
            }

            @Override
            public void onError(String utteranceId) {
                // Speech error
            }
        });
        InitializeView();
    }
    private void InitializeView() {
        binding.send.setOnClickListener(this);
        binding.imageCamera.setOnClickListener(this);
        binding.clearImage.setOnClickListener(this);
        binding.txtToSpeech.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==binding.send.getId()){
            SetUserQuery();
        }
        if(v.getId()==binding.imageCamera.getId()){
            checkCameraPermissionAndOpenCamera();
        }
        if(v.getId()==binding.clearImage.getId()){
            ClearImages();
        }
        if(v.getId()==binding.txtToSpeech.getId()){
            if(ansByAi.isEmpty() || ansByAi.length()<=0){
                Toast.makeText(this, "You have not talked with AI yet...", Toast.LENGTH_SHORT).show();
            }else{
                speak(ansByAi);
            }
        }
    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // TextToSpeech is initialized successfully
            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "TextToSpeech initialization failed", Toast.LENGTH_SHORT).show();
        }
    }
    private void speak(String text) {
        if (textToSpeech != null && !text.isEmpty()) {
            // Use the text-to-speech engine to speak the input text
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to use the camera", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private void SetUserQuery() {
        String userMsg=binding.enterMessage.getText().toString();
        if(!userMsg.isEmpty()){
            long timeStamp = System.currentTimeMillis();
            MessageModel messageModel=new MessageModel("1",userMsg,timeStamp);
            messageModels.add(messageModel);

            chatAdapter=new ChatAdapter(messageModels,this);

            binding.chatRecyclerView.setAdapter(chatAdapter);

            LinearLayoutManager layoutManager=new LinearLayoutManager(this);
            binding.chatRecyclerView.setLayoutManager(layoutManager);

            binding.enterMessage.getText().clear();
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.enterMessage.setEnabled(false);
            binding.enterMessage.setClickable(false);
            binding.enterMessage.setHint("");
            chatAdapter.notifyItemInserted(messageModels.size()-1);
            AskAi(userMsg);
        }
    }

    private void AskAi(String userMsg) {
        String apiKey = BuildConfig.API_KEY;
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", apiKey);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        if(imageList.size()>2){
            Toast.makeText(this, "Currently we can take Only Two Images \uD83D\uDE14", Toast.LENGTH_SHORT).show();
        }
        else if(imageList.size()==1){
            content=new Content.Builder()
                    .addImage(imageList.get(0))
                    .addText(userMsg)
                    .build();
        }else if(imageList.size()==2){
            content=new Content.Builder()
                    .addImage(imageList.get(0))
                    .addImage(imageList.get(1))
                    .addText(userMsg)
                    .build();
        }
        else {
            content = new Content.Builder()
                    .addText(userMsg)
                    .build();
        }
        
        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                ansByAi=resultText;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        binding.progressBar.setVisibility(View.GONE);
                        binding.enterMessage.setEnabled(true);
                        binding.enterMessage.setClickable(true);
                        binding.enterMessage.setHint(R.string.message);

                        long timeStamp = System.currentTimeMillis();
                        MessageModel messageModel=new MessageModel("2",resultText,timeStamp);
                        messageModels.add(messageModel);
                        chatAdapter.notifyItemInserted(messageModels.size()-1);
                    }
                });
                assert resultText != null;
                Log.d("API_RESPONSE", resultText);
            }
            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.enterMessage.setEnabled(true);
                        binding.enterMessage.setClickable(true);
                        binding.enterMessage.setHint(R.string.message);

                        Toast.makeText(AiChatFragment.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, executor);
    }

    private void SetImages(Bitmap Imagebitmap) {
        RecyclerView recyclerView = binding.recImages;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        imageList.add(Imagebitmap);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
    }
    
    private void ClearImages() {
        RecyclerView recyclerView = binding.recImages;
        recyclerView.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        imageList.clear();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shutdown TextToSpeech when activity is destroyed
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (chatAdapter != null) {
            chatAdapter.stopSpeaking();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (chatAdapter != null) {
            chatAdapter.stopSpeaking();
        }
    }
}