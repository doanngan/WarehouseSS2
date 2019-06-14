package src.model;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.util.Tuple;

public class ExportBillPerItem {

	@DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
	private int id;
	private static int idCounter = 0;
	@DAttr(name = "product", type = Type.Domain, length = 5, optional = false)
	@DAssoc(ascName = "product-import", role = "import", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Product.class, cardMin = 1, cardMax = 1), dependsOn = true)
	private Product product;

	@DAttr(name = "equantity", type = Type.Integer, length = 10, optional = true, min = 0.0)
	private Integer equantity;

	@DAttr(name = "eprice", type = Type.Double, length = 15, optional = true, min = 0.0)
	private Double eprice;

	@DAttr(name = "exportChecker", type = Type.Domain, length = 5, optional = false)
	@DAssoc(ascName = "product-exportChecker", role = "exportChecker", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = ExportChecker.class, cardMin = 1, cardMax = 1), dependsOn = true)
	private ExportChecker exportChecker;

	@DAttr(name = "exportBill", type = Type.Domain, length = 5, optional = false)
	@DAssoc(ascName = "exportBill-has-exportBillPerItem", role = "exportBill", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = ExportBill.class, cardMin = 1, cardMax = 1), dependsOn = true)
	private ExportBill exportBill;

//	@DAttr(name = "totalPrice",type=Type.Double,auto=true,mutable = false,optional = true,
//		      serialisable=false,
//		      derivedFrom={"equantity", "eprice"})
	@DAttr(name = "totalPrice", type = Type.Double, auto = true, mutable = false, optional = true, serialisable = false, derivedFrom = {
			"iquantity", "iprice" })
	private Double totalPrice;
	// private StateHistory<String, Object> stateHist;

	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	@DOpt(type = DOpt.Type.RequiredConstructor)
	public ExportBillPerItem(@AttrRef("product") Product p, @AttrRef("equantity") Integer equantity,
			@AttrRef("eprice") Double eprice, @AttrRef("exportChecker") ExportChecker exportChecker,
			@AttrRef("exportBill") ExportBill exportBill) throws ConstraintViolationException {
		this(null, p, equantity, eprice, exportChecker, exportBill);

	}

	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public ExportBillPerItem(Integer id, Product p, Integer equantity, Double eprice, ExportChecker exportChecker,
			ExportBill exportBill) throws ConstraintViolationException {
		this.id = nextID(id);
		this.product = p;
		this.equantity = equantity;
		this.eprice = eprice;
		this.exportChecker = exportChecker;
		this.exportBill = exportBill;
		calTotal();
	}

	public int getId() {
		return id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getEquantity() {
		return equantity;
	}

	public void setIquantity(Integer equantity) {
		this.equantity = equantity;
		// updateTotalPrice();
	}

	public Double getEprice() {
		return eprice;
	}

	public void setEprice(Double eprice) {
		this.eprice = eprice;
		// updateTotalPrice();
	}

	public ExportChecker getExportChecker() {
		return exportChecker;
	}

	public void setExportChecker(ExportChecker exportChecker) {
		this.exportChecker = exportChecker;
	}

	public void calTotal() {
		totalPrice = equantity * eprice;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setExportBill(ExportBill exportBill) {
		this.exportBill = exportBill;
	}

	public ExportBill getExportBill() {
		return exportBill;
	}

	// override toString
	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean full) {
		if (full)
			return "ExportBillPerItem(" + exportBill + ")";
		else
			return "ExportBillPerItem(" + getId() + "," + exportBill.getId() + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		ExportBillPerItem other = (ExportBillPerItem) obj;
		if (id != other.id)
			return false;
		return true;
	}

	private static int nextID(Integer currID) {
		if (currID == null) { // generate one
			idCounter++;
			return idCounter;
		} else { // update
			int num;
			num = currID.intValue();

			// if (num <= idCounter) {
			// throw new
			// ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE,
			// "Lỗi giá trị thuộc tính ID: {0}", num + "<=" + idCounter);
			// }

			if (num > idCounter) {
				idCounter = num;
			}
			return currID;
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
			// check the right attribute
			if (attrib.name().equals("id")) {
				int maxIdVal = (Integer) maxVal;
				if (maxIdVal > idCounter)
					idCounter = maxIdVal;
			}
			// TODO add support for other attributes here
		}
	}

	// implements Comparable interface
	public int compareTo(Object o) {
		if (o == null || (!(o instanceof ExportBillPerItem)))
			return -1;

		ExportBillPerItem e = (ExportBillPerItem) o;

		return this.exportBill.getId().compareTo(e.exportBill.getId());
	}

}
