package de.dada.praisification;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.pdfjet.*;

import de.dada.praisification.model.DAO;
import de.dada.praisification.model.ProtocolContent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
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
        MenuItem actionExportAll = menu.findItem(R.id.actionExport);
        actionExportAll.setOnMenuItemClickListener(this);
                
        return super.onCreateOptionsMenu(menu);
    }

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
        	case R.id.actionNewHost:
        		LayoutInflater li = LayoutInflater.from(this);
				View promptsView = li.inflate(R.layout.dialog, null);
 
				AlertDialog.Builder builder = new AlertDialog.Builder(
						this);
 
				// set dialog.xml to alert dialog builder
				builder.setView(promptsView);
 
				final EditText userInput = (EditText) promptsView
						.findViewById(R.id.editTextDialogUserInput);
 
				// set dialog message
				builder
					.setCancelable(false)
					.setPositiveButton(getResources().getText(R.string.sOK),
					  new DialogInterface.OnClickListener() {
					    @Override
						public void onClick(DialogInterface dialog,int id) {
							// get user input and set it to result
							// edit text
							hostname = userInput.getText().toString();
							if (!hostname.equals(""))
							{
								getDao().createProtocol(hostname);
								updateHostList(hostname);
								Intent detailIntent = new Intent(getApplicationContext(), PersonDetailActivity.class);
						        detailIntent.putExtra(PersonDetailActivity.ARG_HOSTNAME, hostname);
						        startActivityForResult(detailIntent, 0);
							}
							else
							{
								Toast toast = Toast.makeText(getApplicationContext(),
										getResources().getString(R.string.sErrorToast).toString(),
										Toast.LENGTH_LONG);
								toast.show();
							}
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
				AlertDialog dialog = builder.create();
				dialog.setCanceledOnTouchOutside(true);
				// show it
				dialog.show();
				break;
        	case R.id.actionExport:
        		if(PROTOCOLLS.size() > 0)
        		{
        			try {
        				exportToPDF();
        			} catch (FileNotFoundException e) {
        				e.printStackTrace();
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        		}
        	default: break;
			}
		
		return false;
	}
	
	private void exportToPDF() throws FileNotFoundException, Exception 
	{
		String state = Environment.getExternalStorageState();
		//check if the external directory is available for writing
		if (!Environment.MEDIA_MOUNTED.equals(state)) 
		{
		return;
		}

		File exportDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DOWNLOADS);

		//if the external storage directory does not exists, we create it
		if (!exportDir.exists()) 
		{
		exportDir.mkdirs();
		}
		File file;
		file = new File(exportDir, "Protocolls.pdf");
		if (file.exists())
			file.delete();

		//PDF is a class of the PDFJET library
		PDF pdf = new PDF(new FileOutputStream(file));

		//instructions to create the pdf file content		
		//first we create a page with portrait orientation
		Page page = new Page(pdf, Letter.LANDSCAPE);

		//font of the title
		Font f1 = new Font(pdf, CoreFont.HELVETICA_BOLD);

		//title: font f1 and color blue
		Calendar c = Calendar.getInstance(); 
		int year = c.get(Calendar.YEAR);
		TextLine title = new TextLine(f1, getResources().getString(R.string.sPDFTitle).toString() 
				+ " " + year + "\n");
		title.setFont(f1);
		title.setColor(Color.blue);

		//center the title horizontally on the page
		title.setPosition(page.getWidth()/2 - title.getWidth()/2, 40f);

		//draw the title on the page
		title.drawOn(page);
		
		Table table = new Table();
		List<List<Cell>> tableData = new ArrayList<List<Cell>>();
		
		List<Cell> columnTitles = new ArrayList<Cell>();
		columnTitles.add(new Cell(f1, getResources().getString(R.string.sNameColumn).toString()));
		columnTitles.add(new Cell(f1, getResources().getString(R.string.sDrinksColumn).toString()));
		columnTitles.add(new Cell(f1, getResources().getString(R.string.sFoodColumn).toString()));
		columnTitles.add(new Cell(f1, getResources().getString(R.string.sExtrasColumn).toString()));
		columnTitles.add(new Cell(f1, getResources().getString(R.string.sBonusColumn).toString()));
		columnTitles.add(new Cell(f1, getResources().getString(R.string.sTreeColumn).toString()));
		columnTitles.add(new Cell(f1, getResources().getString(R.string.sArrivalColumn).toString()));
		columnTitles.add(new Cell(f1, getResources().getString(R.string.sDepatureColumn).toString()));
		
		//light gray background and center alignment
		for(int i = 0; i < columnTitles.size(); i++) 
		{
		   ((Cell) columnTitles.get(i)).setBgColor(Color.lightyellow);
		   ((Cell) columnTitles.get(i)).setTextAlignment(Align.CENTER);
		}
		tableData.add(columnTitles);
		
		for(ProtocolContent protocol: PROTOCOLLS)
		{			
			//next record in the table
		    List<Cell> record = new ArrayList<Cell>();
		    Font f2 = new Font(pdf, CoreFont.HELVETICA);
		    //create Cells and add them to the record
		    Cell nameCell = new Cell(f2, protocol.getName());
		    nameCell.setTextAlignment(Align.CENTER);
		    record.add(nameCell);
		    Cell drinksCell = new Cell(f2, protocol.getDrinks());
		    drinksCell.setTextAlignment(Align.CENTER);
		    record.add(drinksCell);
		    Cell foodCell = new Cell(f2, protocol.getFood());
		    foodCell.setTextAlignment(Align.CENTER);
		    record.add(foodCell);
		    Cell extrasCell = new Cell(f2, protocol.getExtras());
		    extrasCell.setTextAlignment(Align.CENTER);
		    record.add(nameCell);
		    Cell bonusCell = new Cell(f2, Float.valueOf(protocol.getRating()).toString());
		    bonusCell.setTextAlignment(Align.CENTER);
		    record.add(bonusCell);
		    Cell treeCell = new Cell(f2, Float.valueOf(protocol.getTreeRating()).toString());
		    treeCell.setTextAlignment(Align.CENTER);
		    record.add(treeCell);
		    Cell arrivalCell = new Cell(f2, protocol.getArrivalTime());
		    arrivalCell.setTextAlignment(Align.CENTER);
		    record.add(arrivalCell);
		    Cell depatureCell = new Cell(f2, protocol.getDepatureTime());
		    depatureCell.setTextAlignment(Align.CENTER);
		    record.add(depatureCell);
		    		   
		    //add the record to the table
		    tableData.add(record);
		}
		//populate the table with our tableData ArrayList
	    table.setData(tableData, Table.DATA_HAS_1_HEADER_ROWS);
	    //auto-adjust column widths to fit the content
	    table.autoAdjustColumnWidths();
	    //each cell can contain more rows
	    table.wrapAroundCellText();

	    table.setPosition(page.getWidth()/2 - table.getWidth()/2, 40f);
	    table.drawOn(page);	
		pdf.flush();

		Toast toast = Toast.makeText(getApplicationContext(),
				getResources().getString(R.string.sActionExportToast).toString() + " " +
				exportDir, Toast.LENGTH_LONG);
		toast.show();
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
                    R.layout.list_item,
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
