package com.example.eventchat.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.wear.widget.WearableRecyclerView;

import com.example.eventchat.R;
import com.example.eventchat.model.Message;

import java.util.ArrayList;

public class MessageAdapter extends
        WearableRecyclerView.Adapter<MessageAdapter.ViewHolder>  {

    private static final String TAG = "CustomRecyclerAdapter";
    private static RecyclerViewClickListener mItemListener;

    ArrayList<Message> mMessages;

    public MessageAdapter(ArrayList<Message> messages, RecyclerViewClickListener itemListener) {
        mMessages = messages;
        mItemListener = itemListener;
    }

    public class ViewHolder extends WearableRecyclerView.ViewHolder implements View.OnClickListener {

        private final RelativeLayout messageContainer;
        private final TextView studentId;
        private final TextView messageContent;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            messageContainer = view.findViewById(R.id.message_container);
            studentId = view.findViewById(R.id.student_id);
            messageContent = view.findViewById(R.id.message_content);
        }

        @Override
        public void onClick(View v) {
            mItemListener.recyclerViewListClicked(v, getLayoutPosition());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Message message = mMessages.get(position);

        viewHolder.messageContent.setText(message.getStudentMessage());
        viewHolder.studentId.setText(String.valueOf(message.getStudentId()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mMessages.size();
    }
}