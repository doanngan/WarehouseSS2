package src.model;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.util.Tuple;

public class Preservation {

	@DAttr(name = "id", id = true, auto = true, type = DAttr.Type.Integer, mutable = false, optional = false, length = 6)
	private Integer id;
	private static int idCounter;

	@DAttr(name = "temperature", length = 5, type = Type.Integer, optional = false)
	private Integer temperature;

	@DAttr(name = "moisture", length = 5, type = Type.Integer, optional = false)
	private Integer moisture;

	@DAttr(name = "preservative", type = DAttr.Type.String, optional = true, length = 20)
	private String preservative;

	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public Preservation(@AttrRef("temperature") Integer temperature, @AttrRef("moisture") Integer moisture,
			@AttrRef("preservative") String preservative

	) {
		this(null, temperature, moisture, preservative);
	}

	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public Preservation(Integer id, Integer temperature, Integer moisture, String preservative) {

		this.id = nextID(id);
		this.temperature = temperature;
		this.moisture = moisture;
		this.preservative = preservative;

	}

	public void setPreservative(String preservative) {
		this.preservative = preservative;
	}


	public void setTemperature(Integer temperature) {
		this.temperature = temperature;
	}

	public void setMoisture(Integer moisture) {
		this.moisture = moisture;
	}

	public Integer getId() {
		return id;
	}

	public String getPreservative() {
		return preservative;
	}


	public Integer getTemperature() {
		return temperature;
	}

	public Integer getMoisture() {
		return moisture;
	}


	private int nextID(Integer currId) {
		if (currId == null) {
			idCounter++;
			return idCounter;
		} else {
			int num = currId.intValue();
			if (num > idCounter)
				idCounter = num;
			return currId;
		}
	}

	@DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
	public static void updateAutoGeneratedValue(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal)
			throws ConstraintViolationException {

		if (minVal != null && maxVal != null) {
			// TODO: update this for the correct attribute if there are more than one auto
			// attributes of this class
			int maxIdVal = (Integer) maxVal;
			if (maxIdVal > idCounter) {
				idCounter = maxIdVal;
			}
		}

	}
}