package pds.cdf;

import java.util.ArrayList;

public class AttributeSet {

	ArrayList<Attribute> mAttributes = new ArrayList<Attribute>();

	public AttributeSet() {
		// TODO Auto-generated constructor stub
	}

	public ArrayList<Attribute> add(Attribute attribute) {
		mAttributes.add(attribute);
		
		return mAttributes;
	}

	/**
	 * Retrieve the description of a global attribute with the given name.
	 * 
	 * @param name	the name of the attribute
	 * 
	 * @return the {@link Attribute} with the matching name or NULL if none found.
	 */
	public Attribute getAttribute(String name) {
		for(Attribute att : mAttributes) {
			if(att.mName.equals(name)) return att;
		}
		
		return null;
	}
	
	/**
	 * Retrieve the value of a global attribute with the given name. 
	 * If the value is multi-value placed the delim string between each value. 
	 * 
	 * @param name	the name of the attribute
	 * @param delim	the delimiter string to place between values.
	 * 
	 * @return the value of the matching attribute or an empty string if no attribute is found.
	 * */
	public String getAttributeValue(String name, String delim) {
		for(Attribute att : mAttributes) {
			if(att.mName.equals(name)) return att.getValueString(delim);
		}
		
		return "";
	}
	
}
