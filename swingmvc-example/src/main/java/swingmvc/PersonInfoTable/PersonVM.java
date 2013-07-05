/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package swingmvc.PersonInfoTable;

import swingmvc.core.ViewModel;

/**
 *
 * @author elwood
 */
public class PersonVM extends ViewModel {
    private String name;
    private Integer age;

    public PersonVM() {
    }

    public PersonVM(String name, int age) {
        this.name = name;
        this.age = age;
    }
   
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (this.name != name) {
            this.name = name;
            raisePropertyChange("name");
        }
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        if (this.age != age) {
            this.age = age;
            raisePropertyChange("age");
        }
    }
    
    
}
