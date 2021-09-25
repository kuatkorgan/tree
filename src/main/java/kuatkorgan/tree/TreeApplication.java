package kuatkorgan.tree;

import kuatkorgan.tree.entity.Tree;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Scanner;

public class TreeApplication {

    private static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("main-connection");
    private static final Scanner IN = new Scanner(System.in);

    public static void main(String[] args) {

        selectSchema();
//        create();
//        move();
//        remove();
    }

    private static void selectSchema() {
        EntityManager manager = FACTORY.createEntityManager();

        TypedQuery<Tree> TreeQuery = manager.createQuery(
                "select t from Tree t order by t.left", Tree.class);
        List<Tree> treeList = TreeQuery.getResultList();

        for (Tree tree : treeList) {
            for (int i = 1; i < tree.getLevel(); i++) {
                System.out.print("- ");
            }
            System.out.println(tree.getName() + " [" + tree.getId() + "]");
        }
    }

    private static void create() {
        EntityManager manager = FACTORY.createEntityManager();

        System.out.print("Введите название категорий: ");
        String newTreeName = IN.nextLine();

        System.out.print("Введите ID категорий к которому хотите добавить: ");
        String parentIdIn = IN.nextLine();
        long parentId = Long.parseLong(parentIdIn);

        try {
            manager.getTransaction().begin();

            Tree parentTree = manager.find(Tree.class, parentId);

            manager
                    .createQuery("update Tree t set t.left = t.left + 2 where  t.left > :right")
                    .setParameter("right", parentTree.getRight())
                    .executeUpdate();

            manager
                    .createQuery("update Tree t set t.right = t.right + 2 where t.right >= :right")
                    .setParameter("right", parentTree.getRight())
                    .executeUpdate();

            Tree newTree = new Tree();
            newTree.setLeft(parentTree.getRight());
            newTree.setRight(parentTree.getRight() + 1);
            newTree.setLevel(parentTree.getLevel() + 1);
            newTree.setName(newTreeName);
            manager.persist(newTree);

            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
        }
    }

    private static void move() {
        EntityManager manager = FACTORY.createEntityManager();

        System.out.print("Введите ID категорий которую хотите переместить: ");
        String moveTreeIdIn = IN.nextLine();
        long moveTreeId = Long.parseLong(moveTreeIdIn);
        Tree moveTree = manager.find(Tree.class, moveTreeId);
        int moveTreeLeft = moveTree.getLeft();
        int moveTreeRight = moveTree.getRight();
        int moveTreeLevel = moveTree.getLevel();
        int size = (moveTreeRight - moveTreeLeft) + 1;

        try {
            manager.getTransaction().begin();

            manager
                    .createQuery("update Tree t set t.left = t.left * -1, t.right = t.right * -1 where t.left >= :left_key and t.left < :right_key")
                    .setParameter("left_key", moveTreeLeft)
                    .setParameter("right_key", moveTreeRight)
                    .executeUpdate();


            manager
                    .createQuery("update Tree t set t.left = t.left - :moveSize where t.left > :right_key")
                    .setParameter("moveSize", size)
                    .setParameter("right_key", moveTreeRight)
                    .executeUpdate();

            manager
                    .createQuery("update Tree t set t.right = t.right - :moveSize where t.right > :right_key")
                    .setParameter("moveSize", size)
                    .setParameter("right_key", moveTreeRight)
                    .executeUpdate();

            System.out.print("Вы выбрали: \"" + moveTree.getName() + "\". Введите ID категорий к которому хотите добавить: ");
            String parentTreeIdIn = IN.nextLine();
            long parentTreeId = Long.parseLong(parentTreeIdIn);
            Tree parentTree = manager.find(Tree.class, parentTreeId);
            int parentTreeRight = parentTree.getRight();
            int parentTreeLevel = parentTree.getLevel();

            manager
                    .createQuery("update Tree t set t.left = t.left + :moveSize where t.left > :right_key")
                    .setParameter("moveSize", size)
                    .setParameter("right_key", parentTreeRight)
                    .executeUpdate();

            manager
                    .createQuery("update Tree t set t.right = t.right + :moveSize where t.right >= :right_key")
                    .setParameter("moveSize", size)
                    .setParameter("right_key", parentTreeRight)
                    .executeUpdate();


            manager
                    .createQuery("update Tree t set t.level = (t.level - :moveLevel) + :parentLevel + 1  where t.left <= :left_key * -1")
                    .setParameter("moveLevel", moveTreeLevel)
                    .setParameter("parentLevel", parentTreeLevel)
                    .setParameter("left_key", moveTreeLeft)
                    .executeUpdate();


            manager
                    .createQuery("update Tree t set t.left = :right_key - :left_key + (t.left * -1) where t.left <= :left_key * -1")
                    .setParameter("right_key", parentTreeRight)
                    .setParameter("left_key", moveTreeLeft)
                    .executeUpdate();

            manager
                    .createQuery("update Tree t set t.right = :right_key - :left_key + (t.right * -1) where t.right < :left_key * -1")
                    .setParameter("right_key", parentTreeRight)
                    .setParameter("left_key", moveTreeLeft)
                    .executeUpdate();

            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
        }
    }

    private static void remove() {
        EntityManager manager = FACTORY.createEntityManager();

        System.out.print("Введите ID категорий которую хотите удалить: ");
        String deleteIdIn = IN.nextLine();
        long deleteId = Long.parseLong(deleteIdIn);

        Tree tree = manager.find(Tree.class, deleteId);

        int left = tree.getLeft();
        int right = tree.getRight();
        int moveSize = (right - left ) + 1;

        try {
            manager.getTransaction().begin();

            manager
                    .createQuery("delete from Tree t where t.left >= :left and t.right <= :right ")
                    .setParameter("left", left )
                    .setParameter("right",right )
                    .executeUpdate();

            manager
                    .createQuery("update Tree t set t.left = t.left - :moveSize where t.left > :right")
                    .setParameter("right", right)
                    .setParameter("moveSize", moveSize)
                    .executeUpdate();

            manager
                    .createQuery("update Tree t set t.right = t.right - :moveSize where t.right > :right")
                    .setParameter("right", right)
                    .setParameter("moveSize", moveSize)
                    .executeUpdate();

            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
        }
    }
}
