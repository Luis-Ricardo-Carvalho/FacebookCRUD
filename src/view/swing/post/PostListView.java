package view.swing.post;

import controller.PostController;
import model.Post;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PostListView extends JDialog implements IPostListView {
    private PostController controller;
    private final PostTableModel tableModel = new PostTableModel();
    private final JTable table = new JTable(tableModel);

    public PostListView(JFrame parent) {
        super(parent, "Posts", true);
        this.controller = new PostController();
        this.controller.setPostListView(this);

        setSize(650, 400);
        setLocationRelativeTo(null);

        JScrollPane scrollPane = new JScrollPane(table);

        table.setRowHeight(36);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);

        JButton addButton = new JButton("Adicionar Post");
        addButton.addActionListener(e -> {
            PostFormView form = new PostFormView(this, null, controller);
            form.setVisible(true);
        });

        // Menu de contexto
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Editar");
        JMenuItem deleteItem = new JMenuItem("Excluir");
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }
            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < table.getRowCount()) {
                        table.setRowSelectionInterval(row, row);
                        popupMenu.show(table, e.getX(), e.getY());
                    }
                }
            }
        });

        editItem.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                Post post = tableModel.getPostAt(row);
                PostFormView form = new PostFormView(this, post, controller);
                form.setVisible(true);
            }
        });

        deleteItem.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                Post post = tableModel.getPostAt(row);
                int confirm = JOptionPane.showConfirmDialog(this, "Excluir post?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    controller.excluirPost(post);
                }
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(addButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        controller.loadPosts();
    }

    @Override
    public void setPostList(List<Post> posts) {
        tableModel.setPosts(posts);
    }

    @Override
    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    // Atualiza lista após cadastro/edição/exclusão
    public void refresh() {
        controller.loadPosts();
    }

    // Tabela de posts
    static class PostTableModel extends AbstractTableModel {
        private final String[] columns = {"Conteúdo", "Autor"};
        private List<Post> posts = new ArrayList<>();

        public void setPosts(List<Post> posts) {
            this.posts = posts;
            fireTableDataChanged();
        }

        public Post getPostAt(int row) {
            return posts.get(row);
        }

        @Override public int getRowCount() { return posts.size(); }

        @Override public int getColumnCount() { return columns.length; }

        @Override public String getColumnName(int col) { return columns[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Post p = posts.get(row);
            switch (col) {
                case 0: return p.getContent();
                case 1: return p.getUser().getName();
                default: return null;
            }
        }
        @Override public boolean isCellEditable(int row, int col) { return false; }
    }
}
