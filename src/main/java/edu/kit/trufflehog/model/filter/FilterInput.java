/*
 * This file is part of TruffleHog.
 *
 * TruffleHog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TruffleHog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TruffleHog.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.kit.trufflehog.model.filter;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.awt.*;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *     The FilterInput class contains the data necessary to create a filter. From this class a filter can be created.
 *     That means the following:
 *     <ul>
 *         <li>
 *             Name: The name of the filter. It has to be unique.
 *         </li>
 *         <li>
 *             Type: The type of the filter. A filter can either be a whitelist or a blacklist, that means all nodes
 *             matched by the filter either count as safe (whitelist) or unsafe (blacklist).
 *         </li>
 *         <li>
 *             Origin: The origin of the filter. A filter can originate from an IP Address, from a MAC Address, or
 *             from the current selection. This indicates upon what criteria the filter filters.
 *         </li>
 *         <li>
 *             Rules: The rules of the filter define what the filter matches. These are regular expressions matching
 *             IP addresses, MAC addresses and more.
 *         </li>
 *         <li>
 *             Color: The color of the filter determines what color a matched node should become.
 *         </li>
 *         <li>
 *             Active: Whether this filter is currently being applied on the network or not.
 *         </li>
 *     </ul>
 * </p>
 *
 * @author Julian Brendl
 * @version 1.0
 */
public class FilterInput implements Serializable {
    // Serializable variables
    private String name;
    private FilterType type;
    private FilterOrigin origin;
    private List<String> rules;
    private Color color;
    private boolean active;

    // Property variables for table view
    private transient StringProperty nameProperty;
    private transient StringProperty typeProperty;
    private transient StringProperty originProperty;
    private transient ObjectProperty<Color> colorProperty;
    private transient ObservableList<String> observableRules;
    private transient BooleanProperty booleanProperty;

    /**
     * <p>
     *     Creates a new FilterInput object that is inactive. That means it will at first not be applied onto the
     *     current network.
     * </p>
     * <p>
     *     <ul>
     *         <li>
     *             Name: The name of the filter. It has to be unique.
     *         </li>
     *         <li>
     *             Type: The type of the filter. A filter can either be a whitelist or a blacklist, that means all nodes
     *             matched by the filter either count as safe (whitelist) or unsafe (blacklist).
     *         </li>
     *         <li>
     *             Origin: The origin of the filter. A filter can originate from an IP Address, from a MAC Address, or
     *             from the current selection. This indicates upon what criteria the filter filters.
     *         </li>
     *         <li>
     *             Rules: The rules of the filter define what the filter matches. These are regular expressions matching
     *             IP addresses, MAC addresses and more.
     *         </li>
     *         <li>
     *             Color: The color of the filter determines what color a matched node should become.
     *         </li>
     *         <li>
     *             Active: Whether this filter is currently being applied on the network or not.
     *         </li>
     *     </ul>
     * </p>
     *
     * @param name The name of this filter.
     * @param type The type of this filter.
     * @param origin The origin of this filter.
     * @param rules The rules that define this filter.
     * @param color The color that a node should become if it matches with the filter.
     */
    public FilterInput(final String name, final FilterType type, final FilterOrigin origin, final List<String> rules,
                       final Color color) {
        this.name = name;
        this.type = type;
        this.origin = origin;
        this.rules = rules;
        this.color = color;
        this.active = false;

        load();
    }

    /**
     * <p>
     *     Gets the name of this filter. It has to be unique.
     * </p>
     *
     * @return The name of this filter.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>
     *     Gets the name property of this filter. It has to be unique.
     * </p>
     *
     * @return The name property of this filter.
     */
    public StringProperty getNameProperty() {
        return nameProperty;
    }

    /**
     * <p>
     *     Gets the type of this filter. A filter can either be a whitelist or a blacklist, that means all nodes
     *     matched by the filter either count as safe (whitelist) or unsafe (blacklist).
     * </p>
     *
     * @return The type of this filter.
     */
    public FilterType getType() {
        return type;
    }

    /**
     * <p>
     *     Gets the type property of this filter. A filter can either be a whitelist or a blacklist, that means all
     *     nodes matched by the filter either count as safe (whitelist) or unsafe (blacklist).
     * </p>
     *
     * @return The type property of this filter.
     */
    public StringProperty getTypeProperty() {
        return typeProperty;
    }

    /**
     * <p>
     *     Gets the origin of the filter. A filter can originate from an IP Address, from a MAC Address, or from the
     *     current selection. This indicates upon what criteria the filter filters.
     * </p>
     *
     * @return The origin of this filter.
     */
    public FilterOrigin getOrigin() {
        return origin;
    }

    /**
     * <p>
     *     Gets the origin property of the filter. A filter can originate from an IP Address, from a MAC Address, or
     *     from the current selection. This indicates upon what criteria the filter filters.
     * </p>
     *
     * @return The origin property of this filter.
     */
    public StringProperty getOriginProperty() {
        return originProperty;
    }

    /**
     * <p>
     *     Gets the set of rules for this filter. The rules of the filter define what the filter matches. These are
     *     regular expressions matching IP addresses, MAC addresses and more.
     * </p>
     *
     * @return The set of rules for this filter.
     */
    public List<String> getRules() {
        return rules;
    }

    /**
     * <p>
     *     Gets the color for this filter. The color of the filter determines what color a matched node should
     *     become.
     * </p>
     *
     * @return The color for this filter.
     */
    public Color getColor() {
        return color;
    }

    /**
     * <p>
     *     Gets the color property for this filter. The color of the filter determines what color a matched node should
     *     become.
     * </p>
     *
     * @return The color property for this filter.
     */
    public ObjectProperty<Color> getColorProperty() {
        return colorProperty;
    }

    /**
     * <p>
     *     Gets the current activity state. That means this method returns true if the filter is currently being applied
     *     to the network, and otherwise false.
     * </p>
     *
     * @return True if the filter is currently being applied o the network, else false.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * <p>
     *     Sets the name of this filter. It has to be unique.
     * </p>
     *
     * @param name The new name of this filter.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>
     *     Sets the type of this filter. A filter can either be a whitelist or a blacklist, that means all nodes
     *     matched by the filter either count as safe (whitelist) or unsafe (blacklist).
     * </p>
     *
     * @param type The type of this filter.
     */
    public void setType(FilterType type) {
        this.type = type;
    }

    /**
     * <p>
     *     Sets the origin of the filter. A filter can originate from an IP Address, from a MAC Address, or from the
     *     current selection. This indicates upon what criteria the filter filters.
     * </p>
     *
     * @param origin The origin of this filter.
     */
    public void setOrigin(FilterOrigin origin) {
        this.origin = origin;
    }

    /**
     * <p>
     *     Sets the set of rules for this filter. The rules of the filter define what the filter matches. These are
     *     regular expressions matching IP addresses, MAC addresses and more.
     * </p>
     *
     * @param rules The set of rules for this filter.
     */
    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    /**
     * <p>
     *     Sets the color for this filter. The color of the filter determines what color a matched node should
     *     become.
     * </p>
     *
     * @param color The color for this filter.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * <p>
     *     Sets the current activity state. That means the parameter sets whether or not the filter will be applied on
     *     the current network.
     * </p>
     *
     * @param active The activity state of the filter.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * <p>
     *     Gets the BooleanProperty behind the activity state. This is mapped to the {@link CheckBoxTableCell} in the
     *     table view in the filters menu.
     * </p>
     *
     * @return the BooleanProperty that is is mapped to the {@link CheckBoxTableCell} in the table view in the filters menu.
     */
    public BooleanProperty getBooleanProperty() {
        return booleanProperty;
    }

    /**
     * <p>
     *     Since {@link Property} objects cannot be serialized, THIS METHOD HAS TO BE CALLED AFTER EACH
     *     DESERIALIZATION OF A FILTERINPUT OBJECT to recreate the connection between the Properties and the
     *     normal values that were serialized.
     * </p>
     */
    public void load() {
        // Instantiate property objects
        nameProperty = new SimpleStringProperty(name);
        typeProperty = new SimpleStringProperty(type.name());
        originProperty = new SimpleStringProperty(origin.name());
        observableRules = FXCollections.observableArrayList();
        observableRules.setAll(rules);
        booleanProperty = new SimpleBooleanProperty(active);


        originProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(FilterOrigin.IP.name())) {
                origin = FilterOrigin.IP;
            } else if (newValue.equals(FilterOrigin.MAC.name())) {
                origin = FilterOrigin.MAC;
            } else {
                origin = FilterOrigin.SELECTION;
            }
        });

        observableRules.addListener((ListChangeListener<String>) c -> {
            rules.clear();
            rules.addAll(observableRules);
        });
    }
}
