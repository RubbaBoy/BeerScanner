<script lang="ts">
  const currentYear = new Date().getFullYear();
</script>

<footer class="bg-gray-900 text-white py-8">
  <div class="container mx-auto px-4">
    <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
      <!-- About section -->
      <div>
        <h3 class="text-xl font-bold mb-4">BeerScanner</h3>
        <p class="text-gray-300">
          Track your favorite beers at local bars and get notified when they become available.
        </p>
      </div>
      
      <!-- Quick links -->
      <div>
        <h3 class="text-xl font-bold mb-4">Quick Links</h3>
        <ul class="space-y-2">
          <li><a href="/" class="text-gray-300 hover:text-amber-400 transition-colors">Home</a></li>
          <li><a href="/bars" class="text-gray-300 hover:text-amber-400 transition-colors">Bars</a></li>
          <li><a href="/beers" class="text-gray-300 hover:text-amber-400 transition-colors">Beers</a></li>
          <li><a href="/login" class="text-gray-300 hover:text-amber-400 transition-colors">Login</a></li>
        </ul>
      </div>
      
      <!-- Contact info -->
      <div>
        <h3 class="text-xl font-bold mb-4">Contact</h3>
        <p class="text-gray-300 mb-2">Have questions or suggestions?</p>
        <a href="mailto:info@beerscanner.com" class="text-amber-400 hover:text-amber-300 transition-colors">
          info@beerscanner.com
        </a>
      </div>
    </div>
    
    <!-- Copyright -->
    <div class="border-t border-gray-800 mt-8 pt-6 text-center text-gray-400">
      <p>&copy; {currentYear} BeerScanner. All rights reserved.</p>
    </div>
  </div>
</footer>