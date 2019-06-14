package src.model;

	import java.util.Collection;
	import java.util.Map;

	import domainapp.basics.core.dodm.dsm.DSMBasic;
	import domainapp.basics.core.dodm.qrm.QRM;
	import domainapp.basics.exceptions.DataSourceException;
	import domainapp.basics.exceptions.NotPossibleException;
	import domainapp.basics.model.Oid;
	import domainapp.basics.model.meta.AttrRef;
	import domainapp.basics.model.meta.DAssoc;
	import domainapp.basics.model.meta.DAssoc.AssocEndType;
	import domainapp.basics.model.meta.DAssoc.AssocType;
	import domainapp.basics.model.meta.DAssoc.Associate;
	import domainapp.basics.model.meta.DAttr;
	import domainapp.basics.model.meta.DAttr.Type;
	import domainapp.basics.model.meta.DClass;
	import domainapp.basics.model.meta.DOpt;
	import domainapp.basics.model.meta.MetaConstants;
	import domainapp.basics.model.meta.Select;
	import domainapp.basics.model.query.Expression.Op;
	import domainapp.basics.model.query.Query;
	import domainapp.basics.model.query.QueryToolKit;
	import domainapp.basics.modules.report.model.meta.Output;
	import src.model.Product;
	import src.model.ProductsByNameReport;

	/**
	 * @overview 
	 * 	Represent the reports about products by name.
	 *
	 */
	@DClass(schema="Warehouseman",serialisable=false) //true
	public class ProductsByNameReport {
	  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
	  private int id;
	  private static int idCounter = 0;

	  /**input: product name */
	  @DAttr(name = "name", type = Type.String, length = 30, optional = false)
	  private String name;
	  
	  /**output: products whose names match {@link #name} */
	  @DAttr(name="products",type=Type.Collection,optional=false, mutable=false,
	      serialisable=false,filter=@Select(clazz=Product.class, 
	      attributes={Product.A_id, Product.A_name,
	    		  Product.A_type, Product.A_provider,
	           Product.A_rptProductByName})
	      ,derivedFrom={"name"}
	      )
	  @DAssoc(ascName="products-by-name-report-has-products",role="report",
	      ascType=AssocType.One2Many,endType=AssocEndType.One,
	    associate=@Associate(type=Product.class,cardMin=0,cardMax=MetaConstants.CARD_MORE
	    ))
	  @Output
	  private Collection<Product> products;

	  /**output: number of products found (if any), derived from {@link #products} */
	  @DAttr(name = "numProducts", type = Type.Integer, length = 20, auto=true, mutable=false)
	  @Output
	  private int numProducts;
	  
	  /**
	   * @effects 
	   *  initialise this with <tt>name</tt> and use {@link QRM} to retrieve from data source 
	   *  all {@link product} whose names match <tt>name</tt>.
	   *  initialise {@link #products} with the result if any.
	   *  
	   *  <p>throws NotPossibleException if failed to generate data source query; 
	   *  DataSourceException if fails to read from the data source
	   * 
	   */
	  @DOpt(type=DOpt.Type.ObjectFormConstructor)
	  @DOpt(type=DOpt.Type.RequiredConstructor)
	  public ProductsByNameReport(@AttrRef("name") String name) throws NotPossibleException, DataSourceException {
	    this.id=++idCounter;
	    this.name = name;
	    
	    doReportQuery();
	  }
	  
	  /**
	   * @effects return name
	   */
	  public String getName() {
	    return name;
	  }

	  /**
	   * @effects <pre>
	   *  set this.name = name
	   *  if name is changed
	   *    invoke {@link #doReportQuery()} to update the output attribute value
	   *    throws NotPossibleException if failed to generate data source query; 
	   *    DataSourceException if fails to read from the data source.
	   *  </pre>
	   */
	  public void setName(String name) throws NotPossibleException, DataSourceException {
//	    boolean doReportQuery = (name != null && !name.equals(this.name));
	    
	    this.name = name;
	    
	    // DONOT invoke this here if there are > 1 input attributes!
	    doReportQuery();
	  }

	  /**
	   * This method is invoked when the report input has be set by the user. 
	   * 
	   * @effects <pre>
	   *   formulate the object query
	   *   execute the query to retrieve from the data source the domain objects that satisfy it 
	   *   update the output attributes accordingly.
	   *  
	   *  <p>throws NotPossibleException if failed to generate data source query; 
	   *  DataSourceException if fails to read from the data source. </pre>
	   */
	  @DOpt(type=DOpt.Type.DerivedAttributeUpdater) 
	  @AttrRef(value="products")
	  public void doReportQuery() throws NotPossibleException, DataSourceException {
	    // the query manager instance
	    
	    QRM qrm = QRM.getInstance();
	    
	    // create a query to look up product from the data source
	    // and then populate the output attribute (products) with the result
	    DSMBasic dsm = qrm.getDsm();
	    
	    //TODO: to conserve memory cache the query and only change the query parameter value(s)
	    Query q = QueryToolKit.createSearchQuery(dsm, Product.class, 
	        new String[] {Product.A_name}, 
	        new Op[] {Op.MATCH}, 
	        new Object[] {"%"+name+"%"});
	    
	    Map<Oid, Product> result = qrm.getDom().retrieveObjects(Product.class, q);
	    
	    if (result != null) {
	      // update the main output data 
	      products = result.values();
	      
	      // update other output (if any)
	      numProducts = products.size();
	    } else {
	      // no data found: reset output
	      resetOutput();
	    }
	  }

	  /**
	   * @effects 
	   *  reset all output attributes to their initial values
	   */
	  private void resetOutput() {
	    products = null;
	    numProducts = 0;
	  }

	  /**
	   * A link-adder method for {@link #products}, required for the object form to function.
	   * However, this method is empty because products have already be recorded in the attribute {@link #products}.
	   */
	  @DOpt(type=DOpt.Type.LinkAdder)
	  public boolean addProduct(Collection<Product> products) {
	    // do nothing
	    return false;
	  }
	  
	  /**
	   * @effects return products
	   */
	  public Collection<Product> getProducts() {
	    return products;
	  }
	  
	  /**
	   * @effects return numproducts
	   */
	  public int getNumProducts() {
	    return numProducts;
	  }

	  /**
	   * @effects return id
	   */
	  public int getId() {
	    return id;
	  }

	  /* (non-Javadoc)
	   * @see java.lang.Object#hashCode()
	   */
	  /**
	   * @effects 
	   * 
	   * @version 
	   */
	  @Override
	  public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + id;
	    return result;
	  }

	  /* (non-Javadoc)
	   * @see java.lang.Object#equals(java.lang.Object)
	   */
	  /**
	   * @effects 
	   * 
	   * @version 
	   */
	  @Override
	  public boolean equals(Object obj) {
	    if (this == obj)
	      return true;
	    if (obj == null)
	      return false;
	    if (getClass() != obj.getClass())
	      return false;
	    ProductsByNameReport other = (ProductsByNameReport) obj;
	    if (id != other.id)
	      return false;
	    return true;
	  }

	  /* (non-Javadoc)
	   * @see java.lang.Object#toString()
	   */
	  /**
	   * @effects 
	   * 
	   * @version 
	   */
	  @Override
	  public String toString() {
	    return "ProductsByNameReport (" + id + ", " + name + ")";
	  }
}
