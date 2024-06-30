package com.nationalhackaton.smartlearningassist.adapters;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nationalhackaton.smartlearningassist.R;
import com.nationalhackaton.smartlearningassist.models.MessageModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements TextToSpeech.OnInitListener {

    private ArrayList<MessageModel> messagemodels;
    private Context context;
    private TextToSpeech textToSpeech;

    private static final int SENDER_VIEW_TYPE = 1;
    private static final int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<MessageModel> messagemodels, Context context) {
        this.messagemodels = messagemodels;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return messagemodels.get(position).getuId().equals("1") ? SENDER_VIEW_TYPE : RECEIVER_VIEW_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SENDER_VIEW_TYPE) {
            view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.sample_reciver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messagemodels.get(position);

        if (holder instanceof SenderViewHolder) {
            SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
            senderViewHolder.senderMsg.setText(messageModel.getMessage());
            setDate(senderViewHolder.senderTime, messageModel.getTimeStamp());

        } else if (holder instanceof ReceiverViewHolder) {
            ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
            receiverViewHolder.receiverMsg.setText(messageModel.getMessage());
            setDate(receiverViewHolder.receiverTime, messageModel.getTimeStamp());
            initializeTextToSpeech();
            receiverViewHolder.speakerImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    speakOut(messageModel.getMessage());
                }
            });
        }
    }

    private void setDate(TextView textView, long timeStamp) {
        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        textView.setText(simpleDateFormat.format(date));
    }

    private void initializeTextToSpeech() {
        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(context.getApplicationContext(), this);
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
        }
    }

    private void speakOut(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Toast.makeText(context, "TextToSpeech not initialized", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return messagemodels.size();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(context, "Language not supported", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "TextToSpeech initialization failed", Toast.LENGTH_SHORT).show();
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsg, receiverTime;
        ImageView speakerImage;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.receiverText);
            receiverTime = itemView.findViewById(R.id.receiverTime);
            speakerImage = itemView.findViewById(R.id.imgSpeaker);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        // Release TextToSpeech resources when adapter is detached
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
    }
    public void stopSpeaking() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }
}
