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
	import src.model.Storage;
	import src.model.StoragesByDateReport;

	/**
	 * @overview 
	 * 	Represent the reports about storages by date.
	 *
	 */
	@DClass(schema="WarehouseMan",serialisable=false) //true
	public class StoragesByDateReport {
	  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
	  private int id;
	  private static int idCounter = 0;

	  /**input: storage date */
	  @DAttr(name = "date", type = Type.String, length = 30, optional = false)
	  private String date;
	  
	  /**output: providers whose dates match {@link #date} */
	  @DAttr(name="Storages",type=Type.Collection,optional=false, mutable=false,
	      serialisable=false,filter=@Select(clazz=Storage.class, 
	      attributes={Storage.S_id, Storage.S_date, 
	    		  Storage.S_product, Storage.S_preservation, Storage.S_stockKeeper,
	    		  Storage.S_rptStorageByDate})
	      ,derivedFrom={"date"}
	      )
	  @DAssoc(ascName="Storages-by-date-report-has-storage",role="report",
	      ascType=AssocType.One2Many,endType=AssocEndType.One,
	    associate=@Associate(type=Storage.class,cardMin=0,cardMax=MetaConstants.CARD_MORE
	    ))
	  @Output
	  private Collection<Storage> Storages;

	  /**output: number of Storages found (if any), derived from {@link #Storages} */
	  @DAttr(name = "numStorages", type = Type.Integer, length = 20, auto=true, mutable=false)
	  @Output
	  private int numStorages;
	  
	  /**
	   * @effects 
	   *  initialise this with <tt>name</tt> and use {@link QRM} to retrieve from data source 
	   *  all {@link Storage} whose dates match <tt>name</tt>.
	   *  initialise {@link #Providers} with the result if any.
	   *  
	   *  <p>throws NotPossibleException if failed to generate data source query; 
	   *  DataSourceException if fails to read from the data source
	   * 
	   */
	  @DOpt(type=DOpt.Type.ObjectFormConstructor)
	  @DOpt(type=DOpt.Type.RequiredConstructor)
	  public StoragesByDateReport(@AttrRef("date") String date) throws NotPossibleException, DataSourceException {
	    this.id=++idCounter;
	    
	    this.date = date;
	    
	    doReportQuery();
	  }
	  
	  /**
	   * @effects return date
	   */
	  public String getDate() {
	    return date;
	  }

	  /**
	   * @effects <pre>
	   *  set this.date = date
	   *  if name is changed
	   *    invoke {@link #doReportQuery()} to update the output attribute value
	   *    throws NotPossibleException if failed to generate data source query; 
	   *    DataSourceException if fails to read from the data source.
	   *  </pre>
	   */
	  public void setDate(String date) throws NotPossibleException, DataSourceException {
//	    boolean doReportQuery = (name != null && !name.equals(this.name));
	    
	    this.date = date;
	    
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
	  @DOpt(type=DOpt.Type.DerivedAttributeUpdater) //
	  @AttrRef(value="Storages")
	  public void doReportQuery() throws NotPossibleException, DataSourceException {
	    // the query manager instance
	    
	    QRM qrm = QRM.getInstance();
	    
	    // create a query to look up Provider from the data source
	    // and then populate the output attribute (Providers) with the result
	    DSMBasic dsm = qrm.getDsm();
	    
	    //TODO: to conserve memory cache the query and only change the query parameter value(s)
	    Query q = QueryToolKit.createSearchQuery(dsm, Storage.class, 
	        new String[] {Storage.S_date}, 
	        new Op[] {Op.MATCH}, 
	        new Object[] {"%"+date+"%"});
	    
	    Map<Oid, Storage> result = qrm.getDom().retrieveObjects(Storage.class, q);
	    
	    if (result != null) {
	      // update the main output data 
	    	Storages = result.values();
	      
	      // update other output (if any)
	      numStorages = Storages.size();
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
		  Storages = null;
	    numStorages = 0;
	  }

	  /**
	   * A link-adder method for {@link #Providers}, required for the object form to function.
	   * However, this method is empty because Storages have already be recorded in the attribute {@link #Storages}.
	   */
	  @DOpt(type=DOpt.Type.LinkAdder)
	  public boolean addStorage(Collection<Storage> Storages) {
	    // do nothing
	    return false;
	  }
	  
	  /**
	   * @effects return Storages
	   */
	  public Collection<Storage> getStorages() {
	    return Storages;
	  }
	  
	  /**
	   * @effects return numStorages
	   */
	  public int getNumStorages() {
	    return numStorages;
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
	    StoragesByDateReport other = (StoragesByDateReport) obj;
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
	    return "StoragesByDateReport (" + id + ", " + date + ")";
	  }
}
