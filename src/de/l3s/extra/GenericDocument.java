package de.l3s.extra;


import java.util.LinkedList;

	/**
	 * This is supposed to be a generic implementation for both AIDA-YAGO CONLL and WP datasets
	 * @author joao
	 *
	 * @param <T>
	 */
public class GenericDocument<T> {
	LinkedList<T> listOfT = null;
	String title;
	String txtContent;
	

	public GenericDocument() {
		super();
		listOfT = new LinkedList<>();
	}

		public void addAnnotation(T ann) {
		listOfT.add(ann);
	}

	/**
	 * @return the listOfAnnotation
	 */
	public LinkedList<T> getListOfAnnotation() {
		return listOfT;
	}

	/**
	 * @param listOfAnnotation
	 *            the listOfAnnotation to set
	 */
	public void setListOfAnnotation(LinkedList<T> listOfAnnotation) {
		this.listOfT = listOfAnnotation;
	}

	public void setTxtContent(String txtContent) {
		this.txtContent = txtContent;

	}

	public String getTxtContent() {
		return txtContent;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}