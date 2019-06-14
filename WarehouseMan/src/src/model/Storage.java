
package src.model;

import java.util.Calendar;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.util.Tuple;
import src.model.StoragesByDateReport;

@DClass(schema="WarehouseMan")
public class Storage {
	
	  public static final String S_date = "date";
	  public static final String S_id = "id";
	  public static final String S_product = "product";
	  public static final String S_preservation = "preservation";
	  public static final String S_stockKeeper = "stockKeeper";
	  public static final String S_rptStorageByDate = "rptStorageByDate";
	
	  // attributes of Storage
	  @DAttr(name = S_id, id = true, type = Type.String, auto = true, length = 6, 
			  mutable = false, optional = false)
	  private String id;
	  //static variable to keep track of storage  id
	  private static int idCounter = 0;

	@DAttr(name = S_date, type = Type.String, length = 30, optional = false)
	private String date;

	@DAttr(name = "product", type = Type.Domain, length = 5, optional = false)
	@DAssoc(ascName = "product-storage", role = "storage", 
	ascType = AssocType.One2Many, endType = AssocEndType.Many, 
	associate = @Associate(type = Product.class, cardMin = 1, cardMax = 1), dependsOn = true)
	private Product product;

	@DAttr(name = "preservation", type = Type.Domain, length = 5, optional = false)
	@DAssoc(ascName = "product-preservation", role = "preservation", 
	ascType = AssocType.One2Many, endType = AssocEndType.Many, 
	associate = @Associate(type = Preservation.class, cardMin = 1, cardMax = 1))
	private Preservation preservation;

	@DAttr(name = "stockKeeper", type = Type.Domain, length = 5, optional = true)
	@DAssoc(ascName = "stockKeeper-has-storages", role = "storage", 
	ascType = AssocType.One2Many, endType = AssocEndType.Many, 
	associate = @Associate(type = StockKeeper.class, cardMin = 1, cardMax = 10))
	private StockKeeper stockKeeper;
	
	@DAttr(name=S_rptStorageByDate,type=Type.Domain, serialisable=false, 
		      virtual=true)
		  private StoragesByDateReport rptStorageByDate;

	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	@DOpt(type=DOpt.Type.RequiredConstructor)
	  public Storage(@AttrRef("date") String date, @AttrRef("product") Product product,
			  @AttrRef("preservation") Preservation preservation, @AttrRef("stockKeeper") StockKeeper stockKeeper) {
	    this(null, date, product, preservation, stockKeeper);
	  }

	@DOpt(type=DOpt.Type.DataSourceConstructor)
	  public Storage(@AttrRef("id") String id, 
	      @AttrRef("date") String date, 
	      @AttrRef("product") Product product,
		  @AttrRef("preservation") Preservation preservation, 
		  @AttrRef("stockKeeper") StockKeeper stockKeeper) 
	  throws ConstraintViolationException {
	    // generate an id
	    this.id = nextID(id);

	    // assign other values
	    this.date = date;
	    this.product = product;
	    this.preservation = preservation;
	    this.stockKeeper = stockKeeper;
	}
	
	public String getId() {
		return id;
	}
	
	  public String getDate() {
	    return date;
	  }
	  
	  public void setDate(String date) {
			this.date = date;
		}
	
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Preservation getPreservation() {
		return preservation;
	}

	public void setPreservation(Preservation preservation) {
		this.preservation = preservation;
	}

	public StockKeeper getStockKeeper() {
		return stockKeeper;
	}

	public void setStockKeeper(StockKeeper stockKeeper) {
		this.stockKeeper = stockKeeper;
	}

	public StoragesByDateReport getRptStorageByDate() {
	    return rptStorageByDate;
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
	    Storage other = (Storage) obj;
	    if (id == null) {
	      if (other.id != null)
	        return false;
	    } else if (!id.equals(other.id))
	      return false;
	    return true;
	  }
	  
	  private String nextID(String id) throws ConstraintViolationException {
		    if (id == null) { // generate a new id
		      if (idCounter == 0) {
		        idCounter = Calendar.getInstance().get(Calendar.YEAR);
		      } else {
		        idCounter++;
		      }
		      return "S" + idCounter;
		    } else {
		      // update id
		      int num;
		      try {
		        num = Integer.parseInt(id.substring(1));
		      } catch (RuntimeException e) {
		        throw new ConstraintViolationException(
		            ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] { id });
		      }
		      
		      if (num > idCounter) {
		        idCounter = num;
		      }
		      
		      return id;
		    }
		  }
	  @DOpt(type=DOpt.Type.AutoAttributeValueSynchroniser)
	  public static void updateAutoGeneratedValue(
	      DAttr attrib,
	      Tuple derivingValue, 
	      Object minVal, 
	      Object maxVal) throws ConstraintViolationException {
	    
	    if (minVal != null && maxVal != null) {
	      //TODO: update this for the correct attribute if there are more than one auto attributes of this class 

	      String maxId = (String) maxVal;
	      
	      try {
	        int maxIdNum = Integer.parseInt(maxId.substring(1));
	        
	        if (maxIdNum > idCounter) // extra check
	          idCounter = maxIdNum;
	        
	      } catch (RuntimeException e) {
	        throw new ConstraintViolationException(
	            ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] {maxId});
	      }
	    }
	  }
	}

