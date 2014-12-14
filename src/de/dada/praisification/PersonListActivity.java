package de.dada.praisification;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.dada.praisification.model.DAO;
import de.dada.praisification.model.ProtocolContent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.app.AlertDialog;
import android.app.ListActivity;




/**
 * An activity representing a list of People. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PersonDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link PersonListFragment} and the item details
 * (if present) is a {@link PersonDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link PersonListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class PersonListActivity extends ListActivity
        implements MenuItem.OnMenuItemClickListener{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */

    public List<String> ITEMS = new ArrayList<String>();
    public List<ProtocolContent> PROTOCOLLS = new ArrayList<ProtocolContent>();
    private DAO dao = new DAO(this);
    private String hostname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_list);
        dao.open();
        PROTOCOLLS = dao.getAllProtocolls();
        for(ProtocolContent p: PROTOCOLLS)
        	ITEMS.add(p.getName());
        	
        setListAdapter(new ArrayAdapter<String>(
                this,
                R.layout.list_item,
                ITEMS));
    }
    /**
     * Callback method from {@link PersonListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        hostname = ITEMS.get(position);
        Intent detailIntent = new Intent(this, PersonDetailActivity.class);
        detailIntent.putExtra(PersonDetailActivity.ARG_HOSTNAME, hostname);
        startActivityForResult(detailIntent, 0);
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        MenuItem actionNewHost = menu.findItem(R.id.actionNewHost);
        actionNewHost.setOnMenuItemClickListener(this);
                
        return super.onCreateOptionsMenu(menu);
    }

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
        	case R.id.actionNewHost:
        		LayoutInflater li = LayoutInflater.from(this);
				View promptsView = li.inflate(R.layout.dialog, null);
 
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						this);
 
				// set dialog.xml to alert dialog builder
				alertDialogBuilder.setView(promptsView);
 
				final EditText userInput = (EditText) promptsView
						.findViewById(R.id.editTextDialogUserInput);
 
				// set dialog message
				alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton(getResources().getText(R.string.sOK),
					  new DialogInterface.OnClickListener() {
					    @Override
						public void onClick(DialogInterface dialog,int id) {
							// get user input and set it to result
							// edit text
							hostname = userInput.getText().toString();
							getDao().createProtocol(hostname);
							updateHostList(hostname);
							Intent detailIntent = new Intent(getApplicationContext(), PersonDetailActivity.class);
					        detailIntent.putExtra(PersonDetailActivity.ARG_HOSTNAME, hostname);
					        startActivityForResult(detailIntent, 0);
					    }
					  })
					.setNegativeButton(getResources().getText(R.string.sCancel),
					  new DialogInterface.OnClickListener() {
					    @Override
						public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					    }
					  });
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
				break;
        	default: break;
			}
		
		return false;
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode,
            Intent data) {
		
    	if(resultCode == -1)
		{
    		PROTOCOLLS =  dao.getAllProtocolls();
    		ITEMS.clear();
    		for(ProtocolContent p: PROTOCOLLS)
            	ITEMS.add(p.getName());
            	
            setListAdapter(new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_activated_1,
                    android.R.id.text1,
                    ITEMS));
            ((ArrayAdapter<String>) getListAdapter()).notifyDataSetChanged();
		}
    }
	
	public void updateHostList(String s) {
		this.ITEMS.add(s);
		((ArrayAdapter<String>) getListAdapter()).notifyDataSetChanged();
	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}
}
