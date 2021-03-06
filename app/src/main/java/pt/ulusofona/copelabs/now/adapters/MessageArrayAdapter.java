package pt.ulusofona.copelabs.now.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.copelabs.now.R;

import java.util.ArrayList;
import java.util.List;

import pt.ulusofona.copelabs.now.models.Message;

/**
 * This class is part of Now@ application. It extends to ArrayAdapter. This class returns a view for
 * each object in a collection of data.
 *
 * @author Omar Aponte (COPELABS/ULHT)
 * @version 1.0
 *          COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:06 PM
 */


public class MessageArrayAdapter extends ArrayAdapter<Message> {

    private List<Message> datos;

    /**
     * Constructor of MessageArrayAapter
     *
     * @param context Context of the application
     * @param datos   List of data collection
     */
    public MessageArrayAdapter(Context context, List<Message> datos) {

        super(context, R.layout.message_inf, datos);
        this.datos = datos;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View item = inflater.inflate(R.layout.message_inf, null);

        TextView lblMessage = item.findViewById(R.id.lblmessage);
        lblMessage.setText(datos.get(position).getmMessage());

        TextView lblSubtitulo = item.findViewById(R.id.lbluser);
        lblSubtitulo.setText(datos.get(position).getmUser());

        TextView lblInterest = item.findViewById(R.id.lblInterest);
        lblInterest.setText("#" + datos.get(position).getmInterest());

        TextView lblDate = item.findViewById(R.id.lbldate);
        lblDate.setText(datos.get(position).getmDate() + "");

        return (item);
    }
}
