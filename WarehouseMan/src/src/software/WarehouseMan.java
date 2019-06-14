package src.software;

import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.software.DomainAppToolSoftware;
import src.model.Address;
import src.model.ExportBill;
import src.model.ExportBillPerItem;
import src.model.ExportChecker;
import src.model.ExportStaff;
import src.model.ImportBill;
import src.model.ImportBillPerItem;
import src.model.ImportChecker;
import src.model.ImportStaff;
import src.model.Preservation;
import src.model.Product;
import src.model.ProductsByNameReport;
import src.model.Provider;
import src.model.ProvidersByNameReport;
import src.model.StockKeeper;
import src.model.Storage;
import src.model.StoragesByDateReport;
import src.model.TypeOfProduct;



public class WarehouseMan extends DomainAppToolSoftware {
  
  // the domain model of software
  private static final Class[] model = {
      Address.class,
	  Product.class,
	  Provider.class,
	  Preservation.class,
      TypeOfProduct.class,
      ImportChecker.class,
      ExportChecker.class,
      ImportStaff.class,
      ExportStaff.class,
      ExportBillPerItem.class,
      ExportBill.class,
      ImportBillPerItem.class,
      ImportBill.class,
      ProductsByNameReport.class,
      ProvidersByNameReport.class,
      StockKeeper.class,
      Storage.class,
      StoragesByDateReport.class
      };
      
  @Override
  protected Class[] getModel() {
    return model;
  }


  public static void main(String[] args) throws NotPossibleException {
    new WarehouseMan().exec(args);
  }
}
