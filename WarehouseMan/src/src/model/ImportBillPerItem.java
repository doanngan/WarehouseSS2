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

public class ImportBillPerItem {
	@DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false) int id;
	private static int idCounter = 0;
	@DAttr(name = "product", type = Type.Domain, length = 5, optional = false)
	@DAssoc(ascName = "product-export", role = "export", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Product.class, cardMin = 1, cardMax = 1), dependsOn = true)
	private Product product;

	@DAttr(name = "iquantity", type = Type.Integer, length = 10, optional = true, min = 0.0)
	private Integer iquantity;

	@DAttr(name = "iprice", type = Type.Double, length = 15, optional = true, min = 0.0)
	private Double iprice;

	@DAttr(name = "importChecker", type = Type.Domain, length = 5, optional = false)
	@DAssoc(ascName = "product-importChecker", role = "importChecker", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = ImportChecker.class, cardMin = 1, cardMax = 1), dependsOn = true)
	private ImportChecker importChecker;

	@DAttr(name = "importBill", type = Type.Domain, length = 5, optional = false)
	@DAssoc(ascName = "importBill-has-importBillPerItem", role = "importBill", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = ImportBill.class, cardMin = 1, cardMax = 1), dependsOn = true)
	private ImportBill importBill;

//	@DAttr(name = "totalPrice",type=Type.Double,auto=true,mutable = false,optional = true,
//		      serialisable=false,
//		      derivedFrom={"equantity", "eprice"})
	@DAttr(name = "totalPrice", type = Type.Double, auto = true, mutable = false, optional = true, serialisable = false, derivedFrom = {
			"iquantity", "iprice" })
	private Double totalPrice;

	// private StateHistory<String, Object> stateHist;

	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	@DOpt(type = DOpt.Type.RequiredConstructor)
	public ImportBillPerItem(@AttrRef("product") Product p, @AttrRef("iquantity") Integer iquantity,
			@AttrRef("iprice") Double iprice, @AttrRef("importChecker") ImportChecker importChecker,
			@AttrRef("importBill") ImportBill importBill)
			throws ConstraintViolationException {
		this(null, p, iquantity, iprice, importChecker, importBill);
	}

	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public ImportBillPerItem(Integer id, Product p, Integer iquantity, Double iprice,
			ImportChecker importChecker, ImportBill importBill) throws ConstraintViolationException {
		this.id = nextID(id);
		this.product = p;
		this.iquantity = iquantity;
		this.iprice = iprice;
		this.importChecker = importChecker;
		this.importBill = importBill;
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


	public Integer getIquantity() {
		return iquantity;
	}

	public void setIquantity(Integer iquantity) {
		this.iquantity = iquantity;
		
	}

	public Double getIprice() {
		return iprice;
	}

	public void setIprice(Double iprice) {
		this.iprice = iprice;
	}

	public ImportChecker getImportChecker() {
		return importChecker;
	}

	public void setImportChecker(ImportChecker importChecker) {
		this.importChecker = importChecker;
	}

	public void calTotal() {
		 totalPrice = iquantity * iprice;
	 }
	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setImportBill(ImportBill importBill) {
		this.importBill = importBill;
	}

	public ImportBill getImportBill() {
		return importBill;
	}
	
	// override toString
	  @Override
	  public String toString() {
	    return toString(false);
	  }

	  public String toString(boolean full) {
	    if (full)
	      return "ImportBillPerItem(" + importBill +  ")";
	    else
	      return "ImportBillPerItem(" + getId() + "," + importBill.getId() + ")";
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
	    ImportBillPerItem other = (ImportBillPerItem) obj;
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
	   *          specified for <tt>derivingValue</tt>, using
	   *          <tt>minVal, maxVal</tt>
	   */
	  @DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
	  public static void updateAutoGeneratedValue(DAttr attrib,
	      Tuple derivingValue, Object minVal, Object maxVal)
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
	    if (o == null || (!(o instanceof ImportBillPerItem)))
	      return -1;

	    ImportBillPerItem e = (ImportBillPerItem) o;

	    return this.importBill.getId().compareTo(e.importBill.getId());
	  }

}
