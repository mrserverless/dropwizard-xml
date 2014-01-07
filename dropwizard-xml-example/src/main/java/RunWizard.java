import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;


@JacksonXmlRootElement(localName = "RunWizard")
public class RunWizard {

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    protected String version;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    protected String wizardName;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "WizardDataElement")
    protected List<WizardDataElement> wizardDataElement;

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the wizardName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWizardName() {
        return wizardName;
    }

    /**
     * Sets the value of the wizardName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWizardName(String value) {
        this.wizardName = value;
    }

    /**
     * Gets the value of the wizardDataElement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wizardDataElement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWizardDataElement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WizardDataElement }
     * 
     * 
     */
    public List<WizardDataElement> getWizardDataElement() {
        if (wizardDataElement == null) {
            wizardDataElement = new ArrayList<WizardDataElement>();
        }
        return this.wizardDataElement;
    }

}
