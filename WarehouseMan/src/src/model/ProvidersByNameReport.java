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
	import src.model.Provider;
	import src.model.ProvidersByNameReport;

	/**
	 * @overview 
	 * 	Represent the reports about providers by name.
	 *
	 */
	@DClass(schema="Warehouseman",serialisable=false) //true
	public class ProvidersByNameReport {
	  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
	  private int id;
	  private static int idCounter = 0;

	  /**input: provider name */
	  @DAttr(name = "name", type = Type.String, length = 30, optional = false)
	  private String name;
	  
	  /**output: providers whose names match {@link #name} */
	  @DAttr(name="Providers",type=Type.Collection,optional=false, mutable=false,
	      serialisable=false,filter=@Select(clazz=Provider.class, 
	      attributes={Provider.P_id, Provider.P_name, Provider.P_phone, Provider.P_email, 
	          Provider.P_address, Provider.P_rptProviderByName})
	      ,derivedFrom={"name"}
	      )
	  @DAssoc(ascName="Providers-by-name-report-has-Provider",role="report",
	      ascType=AssocType.One2Many,endType=AssocEndType.One,
	    associate=@Associate(type=Provider.class,cardMin=0,cardMax=MetaConstants.CARD_MORE
	    ))
	  @Output
	  private Collection<Provider> Providers;

	  /**output: number of Providers found (if any), derived from {@link #Providers} */
	  @DAttr(name = "numProviders", type = Type.Integer, length = 20, auto=true, mutable=false)
	  @Output
	  private int numProviders;
	  
	  /**
	   * @effects 
	   *  initialise this with <tt>name</tt> and use {@link QRM} to retrieve from data source 
	   *  all {@link Provider} whose names match <tt>name</tt>.
	   *  initialise {@link #Providers} with the result if any.
	   *  
	   *  <p>throws NotPossibleException if failed to generate data source query; 
	   *  DataSourceException if fails to read from the data source
	   * 
	   */
	  @DOpt(type=DOpt.Type.ObjectFormConstructor)
	  @DOpt(type=DOpt.Type.RequiredConstructor)
	  public ProvidersByNameReport(@AttrRef("name") String name) throws NotPossibleException, DataSourceException {
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
	  @DOpt(type=DOpt.Type.DerivedAttributeUpdater) //
	  @AttrRef(value="Providers")
	  public void doReportQuery() throws NotPossibleException, DataSourceException {
	    // the query manager instance
	    
	    QRM qrm = QRM.getInstance();
	    
	    // create a query to look up Provider from the data source
	    // and then populate the output attribute (Providers) with the result
	    DSMBasic dsm = qrm.getDsm();
	    
	    //TODO: to conserve memory cache the query and only change the query parameter value(s)
	    Query q = QueryToolKit.createSearchQuery(dsm, Provider.class, 
	        new String[] {Provider.P_name}, 
	        new Op[] {Op.MATCH}, 
	        new Object[] {"%"+name+"%"});
	    
	    Map<Oid, Provider> result = qrm.getDom().retrieveObjects(Provider.class, q);
	    
	    if (result != null) {
	      // update the main output data 
	      Providers = result.values();
	      
	      // update other output (if any)
	      numProviders = Providers.size();
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
	    Providers = null;
	    numProviders = 0;
	  }

	  /**
	   * A link-adder method for {@link #Providers}, required for the object form to function.
	   * However, this method is empty because Providers have already be recorded in the attribute {@link #Providers}.
	   */
	  @DOpt(type=DOpt.Type.LinkAdder)
	  public boolean addProvider(Collection<Provider> Providers) {
	    // do nothing
	    return false;
	  }
	  
	  /**
	   * @effects return Providers
	   */
	  public Collection<Provider> getProviders() {
	    return Providers;
	  }
	  
	  /**
	   * @effects return numProviders
	   */
	  public int getNumProviders() {
	    return numProviders;
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
	    ProvidersByNameReport other = (ProvidersByNameReport) obj;
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
	    return "ProvidersByNameReport (" + id + ", " + name + ")";
	  }
}
