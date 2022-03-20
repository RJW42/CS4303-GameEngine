package GameEngine.Utils;


public class Pair<T, S> {
   public T first;
   public S second;

   public Pair(T first, S second) {
      this.first = first;
      this.second = second;
   }

   public T getFirst() {
      return first;
   }

   public S getSecond() {
      return second;
   }

}
