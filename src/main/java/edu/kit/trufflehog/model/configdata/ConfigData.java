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

package edu.kit.trufflehog.model.configdata;

import edu.kit.trufflehog.model.FileSystem;
import edu.kit.trufflehog.model.filter.FilterInput;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.util.Locale;

/**
 * <p>
 *     The ConfigData saves all configurations of TruffleHog into an xml file and continuously updates it.
 *     This is done through the JavaFX {@link Property} object using its bindings. Further more, filter options are
 *     stored separately from the xml file. At the start of the program, everything is loaded from the hard drive into
 *     memory.
 * </p>
 *
 * @author Julian Brendl
 * @version 1.0
 */
public class ConfigData implements IConfig {
    private final ConfigDataModel<StringProperty> settingsDataModel;
    private final ConfigDataModel<String> propertiesDataModel;
    private final FilterDataModel filterDataModel;

    /**
     * <p>
     *     Creates a new ConfigData object.
     * </p>
     *
     * @param fileSystem The {@link FileSystem} object that gives access to relevant folders on the hard-drive.
     * @throws NullPointerException Thrown when it was impossible to get config data for some reason.
     */
    public ConfigData(final FileSystem fileSystem) throws NullPointerException {
        // This has to be loaded first because other data models rely on it (like the property data model)
        this.settingsDataModel = new SettingsDataModel(fileSystem);

        // The settingsDataModel needs to have loaded first
        Locale locale = new Locale(getSetting(String.class, "language").getValue());
        this.propertiesDataModel = new PropertiesDataModel(locale, fileSystem);

        // Both the settings data model and the properties data model need to have loaded first
        this.filterDataModel = new FilterDataModel(fileSystem);

        // VERY IMPORTANT: This makes sure that we can map the filter activity state to a check box in the
        // table view in the filters menu
        filterDataModel.getAllFilters().forEach(filter -> filter.load(this));

    }

    /**
     * <p>
     *     Updates a {@link FilterInput} entry in the database by deleting it and adding it again.
     * </p>
     *
     * @param filterInput The {@link FilterInput} to update.
     */
    public void updateFilterInput(final FilterInput filterInput) {
        filterDataModel.updateFilterInDatabase(filterInput);
    }

    /**
     * <p>
     *     Updates a {@link FilterInput} entry in the database by deleting it and adding it again.
     *     When the name changes, things get more complicated, because the database is index by names. Thus the old
     *     entry has to be removed before the new one is added. Since this has to be done synchronously, it requires
     *     an extra method, because the default is asynchronous.
     * </p>
     *
     * @param filterInput The {@link FilterInput} to update.
     */
    public void updateFilterInput(final FilterInput filterInput, final String newName) {
        filterDataModel.updateFilterInDatabase(filterInput, newName);
    }

    /**
     * <p>
     *     Adds a {@link FilterInput} to the database.
     * </p>
     *
     * @param filterInput The {@link FilterInput} to add to the database.
     */
    public void addFilterInput(final FilterInput filterInput) {
        filterDataModel.addFilterToDatabaseAsynchronous(filterInput);
    }

    /**
     * <p>
     *     Removes a {@link FilterInput} from the database.
     * </p>
     *
     * @param filterInput The {@link FilterInput} to remove from the database.
     */
    public void removeFilterInput(final FilterInput filterInput) {
        filterDataModel.removeFilterFromDatabaseAsynchronous(filterInput);
    }

    /**
     * <p>
     *     Gets all loaded {@link FilterInput} objects. If none have been loaded yet, the method loads them first.
     * </p>
     *
     * @return The list of loaded {@link FilterInput} objects.
     */
    public ObservableList<FilterInput> getAllLoadedFilters() {
        return filterDataModel.getAllFilters();
    }

    @Override
    public StringProperty getSetting(final Class typeClass, final String key) {
        return settingsDataModel.get(typeClass, key);
    }

    @Override
    public String getProperty(final String key) {
        return propertiesDataModel.get(key);
    }

    /**
     * <p>
     *     Closes all connections to all databases and other connections that should be closed before the program exits.
     * </p>
     */
    public void close() {
        filterDataModel.close();
    }
}
