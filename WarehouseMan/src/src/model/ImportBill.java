package src.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.Select;
import domainapp.basics.util.Tuple;

public class ImportBill {

	final static String DATE_FORMAT = "dd/MM/yyyy";
	private static int idCounter = 0;
	@DAttr(name = "id", id = true, auto = true, type = Type.String, length = 6, mutable = false, optional = false)
	String id;

	@DAttr(name = "importStaff", type = Type.Domain, length = 5, optional = false)
	@DAssoc(ascName = "product-importStaff", role = "importStaff", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = ImportStaff.class, cardMin = 1, cardMax = 1), dependsOn = true)
	private ImportStaff importStaff;

	@DAttr(name = "date", type = Type.String, length = 30, optional = false)
	private String date;

	@DAttr(name = "importBillPerItem", type = Type.Collection, optional = false, serialisable = false, filter = @Select(clazz = ImportBillPerItem.class))
	@DAssoc(ascName = "importBill-has-importBillPerItem", role = "importBill", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = ImportBillPerItem.class, cardMin = 0, cardMax = 30))
	private Collection<ImportBillPerItem> importBillPerItem;

	private int count;

	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	@DOpt(type = DOpt.Type.RequiredConstructor)
	public ImportBill(@AttrRef("importStaff") ImportStaff importStaff, @AttrRef("date") String date) {
		this(null, importStaff, date);
	}

	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public ImportBill(String id, ImportStaff importStaff, String date) {
		this.id = nextID(id);
		this.importStaff = importStaff;
		if (validateDate(date)) {
			this.date = date;
		}
		importBillPerItem = new ArrayList<>();
		count = 0;
	}

	public ImportStaff getImportStaff() {
		return importStaff;
	}

	public void setImportStaff(ImportStaff importStaff) {
		this.importStaff = importStaff;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		if (validateDate(date)) {
			this.date = date;
		}
	}

	public String getId() {
		return id;
	}

	public static boolean validateDate(String date) throws ConstraintViolationException {
		try {
			DateFormat df = new SimpleDateFormat(DATE_FORMAT);
			df.setLenient(false);
			df.parse(date);
			return true;
		} catch (ParseException e) {
			throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
					new Object[] { date });

		}
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addImportBillPerItem(ImportBillPerItem d) {
		if (!importBillPerItem.contains(d))
			importBillPerItem.add(d);

		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewImportBillPerItem(ImportBillPerItem d) {
		importBillPerItem.add(d);

		count++;

		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	// @MemberRef(name="enrolments")
	public boolean addImportBillPerItem(Collection<ImportBillPerItem> deos) {
		boolean added = false;
		for (ImportBillPerItem d : deos) {
			if (!importBillPerItem.contains(d)) {
				if (!added)
					added = true;
				importBillPerItem.add(d);
			}
		}
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewImportBillPerItem(Collection<ImportBillPerItem> deos) {
		importBillPerItem.addAll(deos);
		count += deos.size();

		return false;
	}

	@DOpt(type = DOpt.Type.LinkRemover)
	public boolean removeImportBillPerItem(ImportBillPerItem d) {
		boolean removed = importBillPerItem.remove(d);

		if (removed) {
			count--;

		}
		return false;
	}

	public void setImportBillPerItem(Collection<ImportBillPerItem> deo) {
		this.importBillPerItem = deo;
		count = deo.size();

	}

	public Collection<ImportBillPerItem> getImportBillPerIteml() {
		return importBillPerItem;
	}

	@DOpt(type = DOpt.Type.LinkCountGetter)
	public int getCount() {
		return count;
		// return enrolments.size();
	}

	@DOpt(type = DOpt.Type.LinkCountSetter)
	public void setCount(int count1) {
		count = count1;
	}

	// override toString
	/**
	 * @effects returns <code>this.id</code>
	 */
	@Override
	public String toString() {
		return toString(true);
	}

//	@Override
	public String toString(boolean full) {
		if (full)
			return "Import(" + id + "," + importStaff + "," + date + ")";
		else
			return "Import(" + id + ")";

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImportBill other = (ImportBill) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String nextID(String id) throws ConstraintViolationException {
		if (id == null) { // generate a new id
			idCounter++;

			return "SO" + idCounter;
		} else {
			// update id
			int num;
			try {
				num = Integer.parseInt(id.substring(2));
			} catch (RuntimeException e) {
				throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
						new Object[] { id });
			}

			if (num > idCounter) {
				idCounter = num;
			}

			return id;
		}
	}

	/**
	 * @requires minVal != null /\ maxVal != null
	 * @effects update the auto-generated value of attribute <tt>attrib</tt>,
	 *          specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
	 */
	@DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
	public static void updateAutoGeneratedValue(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal)
			throws ConstraintViolationException {

		if (minVal != null && maxVal != null) {
			// TODO: update this for the correct attribute if there are more than one auto
			// attributes of this class

			String maxId = (String) maxVal;

			try {
				int maxIdNum = Integer.parseInt(maxId.substring(1));

				if (maxIdNum > idCounter) // extra check
					idCounter = maxIdNum;

			} catch (RuntimeException e) {
				throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
						new Object[] { maxId });
			}
		}
	}

}
