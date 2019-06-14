package src.model;
import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.util.Tuple;

public class ImportChecker {
	@DAttr(name = "id", id = true, auto = true, type = DAttr.Type.Integer, mutable = false, optional = false, length = 6)
	private Integer id;
	private static int idCounter;

	@DAttr(name = "name", type = DAttr.Type.String, optional = false, length = 20)
	private String name;
	@DAttr(name = "phone", type = DAttr.Type.String, optional = false, length = 15)
	private String phone;
	@DAttr(name = "email", type = DAttr.Type.String, optional = false, length = 20)
	private String email;
	@DAttr(name = "address", type = Type.Domain, length = 20, optional = true)
	@DAssoc(ascName = "importchecker-has-address", role = "importchecker", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Address.class, cardMin = 1, cardMax = 10))
	private Address address;

	@DOpt(type = DOpt.Type.ObjectFormConstructor)

	public ImportChecker(@AttrRef("name") String name, @AttrRef("phone") String phone, @AttrRef("email") String email,
			@AttrRef("address") Address address) {
		this(null, name, phone, email, address);
	}

	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public ImportChecker(Integer id, String name, String phone, String email, Address address) {
		this.id = nextID(id);
		this.name = name;
		if(validatePhone(phone)) {
			this.phone = phone;
			}
		if (validateEmail(email)) {
			this.email = email;
		}
		this.address = address;

	}

	public Integer getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setPhone(String phone) {
		if(validatePhone(phone)) {
		this.phone = phone;
		}
	}

	public String getPhone() {
		return phone;
	}

	public void setEmail(String email) {
		if (validateEmail(email)) {
			this.email = email;
		}

	}

	public String getEmail() {
		return email;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public boolean validatePhone(String phone) throws ConstraintViolationException{
        // First validate that the phone number is not null and has a length of 8
        if (null == phone || phone.length() != 10) {
        	throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE,
					new Object[] { phone });
        }
        // Next check the first character of the string to make sure it's an 8 or 9
        if (phone.charAt(0) != '0') {
        	throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE,
					new Object[] { phone });	        }
        // Now verify that each character of the string is a digit
        for (char c : phone.toCharArray()) {
            if (!Character.isDigit(c)) {
                // One of the characters is not a digit (e.g. 0-9)
            	throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE,
						new Object[] { phone });
            }
        }
        // At this point you know it is valid
        return true;
    }


	public boolean validateEmail(String email) throws ConstraintViolationException {
		String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		if (email.matches(regex))
			return true;
		else {
			throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE,
					new Object[] { email });

		}

	}
	private static int nextID(Integer currID) {
		if (currID == null) {
			idCounter++;
			return idCounter;
		} else {
			int num = currID.intValue();
			if (num > idCounter)
				idCounter = num;

			return currID;
		}
	}

	@DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
	public static void updateAutoGeneratedValue(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal)
			throws ConstraintViolationException {

		if (minVal != null && maxVal != null) {

			int maxIdVal = (Integer) maxVal;
			if (maxIdVal > idCounter) {
				idCounter = maxIdVal;
			}
		}
	}
}
