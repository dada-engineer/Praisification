package de.dada.praisification.model;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ProtocolContent {
    
    private String name;
    private String drinks;  // Comma separated String
    private String food;	// Comma separated String
    private String extras;	// Comma separated String
    private String arrivalTime;
    private String depatureTime;
    private String picturePath;
    private float bonus;
    private float treeRating;
    
    public ProtocolContent(String name) {
		this.setName(name);
		this.setDrinks("");
	    this.setFood("");
	    this.setExtras("");
	    this.setArrivalTime("");
	    this.setDepatureTime("");
	    this.setPicturePath("");
	    this.setRating(0);
	    this.setTreeRating(0);
	}

	public String getPicturePath() {
		return picturePath;
	}

	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}

	public String getDepatureTime() {
		return depatureTime;
	}

	public void setDepatureTime(String depatureTime) {
		this.depatureTime = depatureTime;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getExtras() {
		return extras;
	}

	public void setExtras(String extras) {
		this.extras = extras;
	}

	public String getFood() {
		return food;
	}

	public void setFood(String food) {
		this.food = food;
	}

	public String getDrinks() {
		return drinks;
	}

	public void setDrinks(String drinks) {
		this.drinks = drinks;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getRating() {
		return bonus;
	}

	public void setRating(float f) {
		this.bonus = f;
	}

	public float getTreeRating() {
		return treeRating;
	}

	public void setTreeRating(float treerating) {
		this.treeRating = treerating;
	}
}
