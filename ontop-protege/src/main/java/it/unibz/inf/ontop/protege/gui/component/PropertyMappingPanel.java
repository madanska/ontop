package it.unibz.inf.ontop.protege.gui.component;

/*
 * #%L
 * ontop-protege4
 * %%
 * Copyright (C) 2009 - 2013 KRDB Research Centre. Free University of Bozen Bolzano.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import it.unibz.inf.ontop.io.PrefixManager;
import it.unibz.inf.ontop.model.OBDAModel;
import it.unibz.inf.ontop.model.Predicate;
import it.unibz.inf.ontop.ontology.DataPropertyExpression;
import it.unibz.inf.ontop.ontology.ObjectPropertyExpression;
import it.unibz.inf.ontop.protege.gui.IconLoader;
import it.unibz.inf.ontop.protege.gui.MapItem;
import it.unibz.inf.ontop.protege.gui.PredicateItem;
import it.unibz.inf.ontop.protege.gui.action.EditableCellFocusAction;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalComboBoxButton;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

public class PropertyMappingPanel extends javax.swing.JPanel {

	private static final long serialVersionUID = 1L;

	private OBDAModel obdaModel;
	private PrefixManager prefixManager;

	private boolean isPredicatePropertyValid = false;
	
	private static final Color SELECTION_BACKGROUND = UIManager.getDefaults().getColor("Table.selectionBackground");
	private static final Color NORMAL_BACKGROUND = new Color(240, 245, 240);
	
	private static final Border EDIT_BORDER = BorderFactory.createCompoundBorder(
			BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(1, 3, 1, 3),
					BorderFactory.createLineBorder(new Color(0, 0, 0), 2)),
			BorderFactory.createEmptyBorder(4, 4, 4, 4));
	private static final Border NORMAL_BORDER = BorderFactory.createCompoundBorder(
			BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(1, 3, 1, 3),
					BorderFactory.createLineBorder(new Color(192, 192, 192), 1)),
			BorderFactory.createEmptyBorder(5, 5, 5, 5));
	
	private static final Font DEFAULT_FONT = new Font("Dialog", Font.PLAIN, 14);
	
	private static Color DEFAULT_TEXTFIELD_BACKGROUND = UIManager.getDefaults().getColor("TextField.background");
	private static Color ERROR_TEXTFIELD_BACKGROUND = new Color(255, 143, 143);
	
	public PropertyMappingPanel(OBDAModel obdaModel) {
		this.obdaModel = obdaModel;
		prefixManager = obdaModel.getPrefixManager();
		initComponents();
	}

	public List<MapItem> getPredicateObjectMapsList() {
		DefaultTableModel propertyListModel = (DefaultTableModel) lstProperties.getModel();
		int column = 0; // it's a single column table
		int totalRow = propertyListModel.getRowCount();
		
		ArrayList<MapItem> predicateObjectMapList = new ArrayList<MapItem>();
		for (int row = 0; row < totalRow; row++) {
			predicateObjectMapList.add((MapItem) propertyListModel.getValueAt(row, column));
		}
		return predicateObjectMapList;
	}
	
	public boolean isEmpty() {
		return getPredicateObjectMapsList().isEmpty();
	}

	public boolean isEditing() {
		return lstProperties.isEditing();
	}

	public boolean stopCellEditing() {
		return lstProperties.getCellEditor().stopCellEditing();
	}
	
	public void clear() {
		cboPropertyAutoSuggest.setSelectedIndex(-1);
		((DefaultTableModel) lstProperties.getModel()).setRowCount(0);
	}
	
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popMenu = new javax.swing.JPopupMenu();
        menuDelete = new javax.swing.JMenuItem();
        pnlAddProperty = new javax.swing.JPanel();
        cmdAdd = new javax.swing.JButton();
        pnlPropertyMapping = new javax.swing.JPanel();
        scrPropertyList = new javax.swing.JScrollPane();
        lstProperties = new javax.swing.JTable();
        lblCurrentPropertyMapping = new javax.swing.JLabel();

        menuDelete.setText("Delete mapping");
        menuDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDeleteActionPerformed(evt);
            }
        });
        popMenu.add(menuDelete);

        setAlignmentX(5.0F);
        setAlignmentY(5.0F);
        setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        setMinimumSize(new java.awt.Dimension(280, 500));
        setPreferredSize(new java.awt.Dimension(280, 500));
        setLayout(new java.awt.BorderLayout());

        pnlAddProperty.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 3, 0));
        pnlAddProperty.setLayout(new java.awt.BorderLayout(3, 0));
        Vector<Object> v = new Vector<Object>();
        for (DataPropertyExpression dp : obdaModel.getOntologyVocabulary().getDataProperties()) {
            v.addElement(new PredicateItem(dp.getPredicate(), prefixManager));
        }
        for (ObjectPropertyExpression op : obdaModel.getOntologyVocabulary().getObjectProperties()) {
            v.addElement(new PredicateItem(op.getPredicate(), prefixManager));
        }
        cboPropertyAutoSuggest = new AutoSuggestComboBox(v);
        cboPropertyAutoSuggest.setRenderer(new PropertyListCellRenderer());
        cboPropertyAutoSuggest.setMinimumSize(new java.awt.Dimension(195, 23));
        cboPropertyAutoSuggest.setPreferredSize(new java.awt.Dimension(195, 23));
        JTextField txtComboBoxEditor = (JTextField) cboPropertyAutoSuggest.getEditor().getEditorComponent();
        txtComboBoxEditor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cboPropertyAutoSuggestKeyPressed(evt);
            }
        });
        pnlAddProperty.add(cboPropertyAutoSuggest);

        cmdAdd.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cmdAdd.setContentAreaFilled(false);
        cmdAdd.setFocusable(false);
        cmdAdd.setMaximumSize(new java.awt.Dimension(23, 23));
        cmdAdd.setMinimumSize(new java.awt.Dimension(23, 23));
        cmdAdd.setPreferredSize(new java.awt.Dimension(23, 23));
        cmdAdd.setIcon(IconLoader.getImageIcon("images/plus.png"));
        cmdAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddActionPerformed(evt);
            }
        });
        pnlAddProperty.add(cmdAdd, java.awt.BorderLayout.EAST);

        add(pnlAddProperty, java.awt.BorderLayout.PAGE_START);

        pnlPropertyMapping.setLayout(new java.awt.BorderLayout());

        lstProperties.setModel(new DefaultTableModel(0, 1));
        lstProperties.setCellSelectionEnabled(true);
        lstProperties.setRowHeight(65);
        lstProperties.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstProperties.setTableHeader(null);
        lstProperties.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lstPropertiesMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lstPropertiesMouseReleased(evt);
            }
        });
        lstProperties.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lstPropertiesKeyPressed(evt);
            }
        });
        new EditableCellFocusAction(lstProperties, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        new EditableCellFocusAction(lstProperties, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        scrPropertyList.setViewportView(lstProperties);

        pnlPropertyMapping.add(scrPropertyList, java.awt.BorderLayout.CENTER);

        lblCurrentPropertyMapping.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblCurrentPropertyMapping.setForeground(new java.awt.Color(53, 113, 163));
        lblCurrentPropertyMapping.setText("Current property mappings:");
        lblCurrentPropertyMapping.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        pnlPropertyMapping.add(lblCurrentPropertyMapping, java.awt.BorderLayout.NORTH);

        add(pnlPropertyMapping, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

	private void menuDeleteActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menuDeleteActionPerformed
		deleteMappingItem();
	}// GEN-LAST:event_menuDeleteActionPerformed

	private void lstPropertiesMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_lstPropertiesMousePressed
		showPopup(evt);
	}// GEN-LAST:event_lstPropertiesMousePressed

	private void lstPropertiesMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_lstPropertiesMouseReleased
		showPopup(evt);
	}// GEN-LAST:event_lstPropertiesMouseReleased

	private void lstPropertiesKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_lstPropertiesKeyPressed
		int code = evt.getKeyCode();
		if (code == KeyEvent.VK_DELETE) {
			deleteMappingItem();
		}
	}// GEN-LAST:event_lstPropertiesKeyPressed

	private void cmdAddActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmdAddActionPerformed
		Object item = cboPropertyAutoSuggest.getSelectedItem();
		addPredicateProperty(item);
		validatePredicateProperty();
	}// GEN-LAST:event_cmdAddActionPerformed

	private void showPopup(MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			popMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}

	private void deleteMappingItem() {
		TableCellEditor editor = lstProperties.getCellEditor();
		if (editor != null) {
			editor.stopCellEditing();
		}
		int index = lstProperties.getSelectedRow();
		if (index != -1) {
			((DefaultTableModel) lstProperties.getModel()).removeRow(index);
		}
	}

	private void cboPropertyAutoSuggestKeyPressed(KeyEvent evt) {
		int code = evt.getKeyCode();
		if (code == KeyEvent.VK_ESCAPE) {
			cboPropertyAutoSuggest.setSelectedIndex(-1);
		} else if (code == KeyEvent.VK_ENTER) {
			Object item = cboPropertyAutoSuggest.getSelectedItem();
			addPredicateProperty(item);
			validatePredicateProperty();
		}
	}

	private void addPredicateProperty(Object item) {
		if (item instanceof PredicateItem) {
			PredicateItem selectedItem = (PredicateItem) item;
			addRow(selectedItem);
			isPredicatePropertyValid  = true;
		} else {
			isPredicatePropertyValid = false;
		}
	}

	private void addRow(PredicateItem selectedItem) {
		MapItem predicateObjectMap = new MapItem(selectedItem);
		if (selectedItem.isObjectPropertyPredicate()) {
			predicateObjectMap.setTargetMapping(defaultUriTemplate());
		}
		
		// Insert the selected item from the combo box to the table as a new table cell
		MapItem[] row = new MapItem[1];
		row[0] = predicateObjectMap;
		((DefaultTableModel) lstProperties.getModel()).addRow(row);
		
		// Define for each added table cell a custom renderer and editor
		TableColumn col = lstProperties.getColumnModel().getColumn(0);
		
		// Add custom cell renderer
		col.setCellRenderer(new PropertyItemRenderer());
		
		// Add custom cell editor
		PropertyItemEditor editor = new PropertyItemEditor();
		editor.addCellEditorListener(lstProperties);
		col.setCellEditor(editor);
	}

	private String defaultUriTemplate() {
		return String.format("%s", getDefaultNamespace());
	}

	private String getDefaultNamespace() {
		String defaultNamespace = prefixManager.getDefaultPrefix();
		return prefixManager.getShortForm(defaultNamespace, false);
	}

	//
	// Methods for validation
	//

	private void validatePredicateProperty() {
		if (isPredicatePropertyValid) {
			setNormalBackground(cboPropertyAutoSuggest);
		} else {
			setErrorBackground(cboPropertyAutoSuggest);
		}
	}

	//
	// Methods for GUI changes
	//

	private void setNormalBackground(JComboBox comboBox) {
		JTextField text = ((JTextField) comboBox.getEditor().getEditorComponent());
		text.setBackground(DEFAULT_TEXTFIELD_BACKGROUND);
	}

	private void setErrorBackground(JComboBox comboBox) {
		JTextField text = ((JTextField) comboBox.getEditor().getEditorComponent());
		text.setBackground(ERROR_TEXTFIELD_BACKGROUND);
		Component[] comp = comboBox.getComponents();
		for (int i = 0; i < comp.length; i++) {// hack valid only for Metal L&F
			if (comp[i] instanceof MetalComboBoxButton) {
				MetalComboBoxButton coloredArrowsButton = (MetalComboBoxButton) comp[i];
				coloredArrowsButton.setBackground(null);
				break;
			}
		}
		comboBox.requestFocus();
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdAdd;
    private javax.swing.JLabel lblCurrentPropertyMapping;
    private javax.swing.JTable lstProperties;
    private javax.swing.JMenuItem menuDelete;
    private javax.swing.JPanel pnlAddProperty;
    private AutoSuggestComboBox cboPropertyAutoSuggest;
    private javax.swing.JPanel pnlPropertyMapping;
    private javax.swing.JPopupMenu popMenu;
    private javax.swing.JScrollPane scrPropertyList;
    // End of variables declaration//GEN-END:variables

	/**
	 * A renderer class to draw the property mapping item in the GUI panel.
	 */
	class PropertyItemRenderer extends JPanel implements TableCellRenderer {

		private static final long serialVersionUID = 1L;
		
		private JPanel pnlPropertyName;
		private JPanel pnlPropertyUriTemplate;
		private JLabel lblPropertyName;
		private DataTypeComboBox cboDataTypes;
		private JLabel lblMapIcon;
		private JTextField txtPropertyTargetMap;

		public PropertyItemRenderer() {
			initComponents();
		}

		private void initComponents() {
			setBackground(NORMAL_BACKGROUND);
			setLayout(new BorderLayout(0, 2));
			setBorder(NORMAL_BORDER);
			setFocusable(false);
			
			pnlPropertyName = new JPanel();
			pnlPropertyUriTemplate = new JPanel();
			lblPropertyName = new JLabel();
			cboDataTypes = new DataTypeComboBox();
			lblMapIcon = new JLabel();
			txtPropertyTargetMap = new JTextField();
			
			lblPropertyName.setFont(DEFAULT_FONT);
			
			cboDataTypes.setBackground(Color.WHITE);
			cboDataTypes.setSelectedIndex(-1);
			
			pnlPropertyName.setLayout(new BorderLayout(5, 0));
			pnlPropertyName.setOpaque(false);
			pnlPropertyName.setFocusable(false);
			pnlPropertyName.add(lblPropertyName, BorderLayout.WEST);
			pnlPropertyName.add(cboDataTypes, BorderLayout.EAST);
			
			lblMapIcon.setIcon(IconLoader.getImageIcon("images/link.png"));
			
			txtPropertyTargetMap.setFont(DEFAULT_FONT);
			
			pnlPropertyUriTemplate.setLayout(new BorderLayout(5, 0));
			pnlPropertyUriTemplate.setOpaque(false);
			pnlPropertyUriTemplate.setFocusable(false);
			pnlPropertyUriTemplate.add(lblMapIcon, BorderLayout.WEST);
			pnlPropertyUriTemplate.add(txtPropertyTargetMap, BorderLayout.CENTER);

			add(pnlPropertyName, BorderLayout.NORTH);
			add(pnlPropertyUriTemplate, BorderLayout.SOUTH);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if (isSelected) {
				setBackground(SELECTION_BACKGROUND);
				// Due to the behavioral override, hasFocus means NORMAL_MODE and !hasFocus means EDIT_MODE
				if (hasFocus) {
					setBorder(NORMAL_BORDER);
				} else {
					setBorder(EDIT_BORDER);
				}
			} else {
				if (hasFocus) {
					setBackground(SELECTION_BACKGROUND);
				} else {
					setBackground(NORMAL_BACKGROUND);
				}
				setBorder(NORMAL_BORDER);
			}
			
			if (value instanceof MapItem) {
				MapItem entry = (MapItem) value;
				lblPropertyName.setText(entry.toString());
				if (entry.isObjectMap()) {
					cboDataTypes.setVisible(true);
					cboDataTypes.setSelectedItem(entry.getDataType());
					lblPropertyName.setIcon(IconLoader.getImageIcon("images/data_property.png"));
					txtPropertyTargetMap.setText(entry.getTargetMapping());
				} else if (entry.isRefObjectMap()) {
					cboDataTypes.setVisible(false);
					lblPropertyName.setIcon(IconLoader.getImageIcon("images/object_property.png"));
					txtPropertyTargetMap.setText(entry.getTargetMapping());
				}
			}
			return this;
		}
	}

	/**
	 * An editor renderer class to draw the property mapping item when users enter the editing mode.
	 */
	public class PropertyItemEditor extends AbstractCellEditor implements TableCellEditor {

		private static final long serialVersionUID = 1L;
		
		private JPanel pnlPropertyMapCell;
		private JPanel pnlPropertyName;
		private JPanel pnlPropertyUriTemplate;
		private JLabel lblPropertyName;
		private DataTypeComboBox cboDataTypes;
		private JLabel lblMapIcon;
		private JTextField txtPropertyTargetMap;
		
		private MapItem editedItem;

		public PropertyItemEditor() {
			initComponents();
		}
		
		private void setCaretToTextField() {
			txtPropertyTargetMap.requestFocusInWindow();
			txtPropertyTargetMap.setCaretPosition(txtPropertyTargetMap.getText().length());
		}

		private void initComponents() {
			pnlPropertyMapCell = new JPanel() {
				@Override
				public void addNotify() {
					super.addNotify();
					// This code overrides the behavior of table item such that the item immediately
					// enters the EDIT_MODE when users make a single click to the item panel.
					setCaretToTextField();
				}
			};
			pnlPropertyMapCell.setRequestFocusEnabled(true);
			pnlPropertyName = new JPanel();
			pnlPropertyUriTemplate = new JPanel();
			lblPropertyName = new JLabel();
			cboDataTypes = new DataTypeComboBox();
			lblMapIcon = new JLabel();
			txtPropertyTargetMap = new JTextField();
			
			pnlPropertyMapCell.setLayout(new BorderLayout(0, 2));
			pnlPropertyMapCell.setBorder(NORMAL_BORDER);
			pnlPropertyMapCell.setRequestFocusEnabled(true);
			
			lblPropertyName.setFont(DEFAULT_FONT);
			
			cboDataTypes.setBackground(Color.WHITE);
			cboDataTypes.setSelectedIndex(-1);
			
			pnlPropertyName.setLayout(new BorderLayout(5, 0));
			pnlPropertyName.setOpaque(false);
			pnlPropertyName.add(lblPropertyName, BorderLayout.WEST);
			pnlPropertyName.add(cboDataTypes, BorderLayout.EAST);
			
			lblMapIcon.setIcon(IconLoader.getImageIcon("images/link.png"));
			
			txtPropertyTargetMap.setFont(DEFAULT_FONT);
			
			pnlPropertyUriTemplate.setLayout(new BorderLayout(5, 0));
			pnlPropertyUriTemplate.setOpaque(false);
			pnlPropertyUriTemplate.add(lblMapIcon, BorderLayout.WEST);
			pnlPropertyUriTemplate.add(txtPropertyTargetMap, BorderLayout.CENTER);
			
			pnlPropertyMapCell.add(pnlPropertyName, BorderLayout.NORTH);
			pnlPropertyMapCell.add(pnlPropertyUriTemplate, BorderLayout.SOUTH);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			pnlPropertyMapCell.setBackground(SELECTION_BACKGROUND);
			if (!isSelected) {
				// Due to the behavioral override, isSelected means NORMAL_MODE and !isSelected means EDIT_MODE
				pnlPropertyMapCell.setBorder(EDIT_BORDER);
			}
			if (value instanceof MapItem) {
				MapItem entry = (MapItem) value;
				lblPropertyName.setText(entry.toString());
				editedItem = entry;
				if (entry.isObjectMap()) {
					cboDataTypes.setVisible(true);
					cboDataTypes.setSelectedItem(entry.getDataType());
					lblPropertyName.setIcon(IconLoader.getImageIcon("images/data_property.png"));
					txtPropertyTargetMap.setText(entry.getTargetMapping());
				} else if (entry.isRefObjectMap()) {
					cboDataTypes.setVisible(false);
					lblPropertyName.setIcon(IconLoader.getImageIcon("images/object_property.png"));
					txtPropertyTargetMap.setText(entry.getTargetMapping());
				}
			}
			return pnlPropertyMapCell;
		}

		@Override
		public Object getCellEditorValue() {
			if (editedItem != null) {
				editedItem.setTargetMapping(txtPropertyTargetMap.getText());
				editedItem.setDataType((Predicate) cboDataTypes.getSelectedItem());
			}
			return editedItem;
		}

		@Override
		public boolean isCellEditable(EventObject anEvent) {
			if (anEvent instanceof MouseEvent) {
				MouseEvent mouseEvent = (MouseEvent) anEvent;
				if (mouseEvent.getClickCount() == 1) {
					return true;
				}
			} else if (anEvent instanceof ActionEvent) {
				return true;
			}
			return false;
		}

		@Override
		public boolean stopCellEditing() {
			try { // handling unknown array out of bound exception (?)
				editedItem.setTargetMapping(txtPropertyTargetMap.getText());
				editedItem.setDataType((Predicate) cboDataTypes.getSelectedItem());
				if (editedItem.isValid()) { // Validate the entry
					setNormalBackground(txtPropertyTargetMap);
				} else {
					setErrorBackground(txtPropertyTargetMap);
					return false;
				}
				return super.stopCellEditing();
			} catch (Exception e) {
				return super.stopCellEditing();
			}
		}

		//
		// Methods for GUI changes
		//

		private void setNormalBackground(JTextField textField) {
			textField.setBackground(DEFAULT_TEXTFIELD_BACKGROUND);
		}

		private void setErrorBackground(JTextField textField) {
			textField.setBackground(ERROR_TEXTFIELD_BACKGROUND);
		}
	}

	/**
	 * Renderer class to present the property list.
	 */
	private class PropertyListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof PredicateItem) {
				PredicateItem property = (PredicateItem) value;
				if (property.isDataPropertyPredicate()) {
					ImageIcon icon = IconLoader.getImageIcon("images/data_property.png");
					label.setIcon(icon);
					label.setText(property.getQualifiedName());
				} else if (property.isObjectPropertyPredicate()) {
					ImageIcon icon = IconLoader.getImageIcon("images/object_property.png");
					label.setIcon(icon);
					label.setText(property.getQualifiedName());
				}
			}
			return label;
		}
	}
}
