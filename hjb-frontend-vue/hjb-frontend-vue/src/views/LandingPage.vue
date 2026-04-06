<template>
  <div class="landing">
    <!-- Navbar -->
    <header class="navbar">
      <div class="navbar-logo">
        <div class="logo-icon">HJB</div>
        <span class="logo-text">Insurance</span>
      </div>
      <nav class="navbar-links">
        <a href="#products">Products</a>
        <a href="#about">About</a>
      </nav>
      <div class="navbar-actions">
        <el-button @click="$router.push('/customer-login')">Sign In</el-button>
        <el-button type="primary" @click="$router.push('/customer-login?tab=register')">Sign Up</el-button>
        <el-button plain @click="$router.push('/login')" size="small">Employee</el-button>
      </div>
    </header>

    <!-- Hero -->
    <section class="hero">
      <div class="hero-content">
        <h1 class="hero-title">Protect What Matters Most</h1>
        <p class="hero-sub">Comprehensive auto and home insurance tailored for you and your family. Get covered in minutes.</p>
        <div class="hero-btns">
          <el-button type="primary" size="large" @click="$router.push('/customer-login?tab=register')">Get a Quote</el-button>
          <el-button size="large" @click="$router.push('/customer-login')">View My Policies</el-button>
        </div>
      </div>
      <div class="hero-visual">🛡️</div>
    </section>

    <!-- Products -->
    <section class="products" id="products">
      <h2 class="section-title">Our Products</h2>
      <div class="product-grid">
        <div class="product-card">
          <div class="product-icon">🚗</div>
          <h3>Auto Insurance</h3>
          <p>Comprehensive coverage for your vehicle including collision, liability, and uninsured motorist protection.</p>
          <ul>
            <li>Collision & Comprehensive</li>
            <li>Liability Coverage</li>
            <li>Roadside Assistance</li>
            <li>Rental Reimbursement</li>
          </ul>
          <el-button type="primary" @click="$router.push('/customer-login?tab=register')">Get Auto Quote</el-button>
        </div>
        <div class="product-card">
          <div class="product-icon">🏠</div>
          <h3>Home Insurance</h3>
          <p>Protect your home and belongings with flexible coverage options for every type of property.</p>
          <ul>
            <li>Dwelling Protection</li>
            <li>Personal Property</li>
            <li>Liability Coverage</li>
            <li>Natural Disaster Add-ons</li>
          </ul>
          <el-button type="primary" @click="$router.push('/customer-login?tab=register')">Get Home Quote</el-button>
        </div>
        <div class="product-card featured">
          <div class="product-badge">Best Value</div>
          <div class="product-icon">⭐</div>
          <h3>Bundle & Save</h3>
          <p>Combine auto and home insurance for up to 20% discount on your total premium.</p>
          <ul>
            <li>Auto + Home combined</li>
            <li>Up to 20% discount</li>
            <li>Single deductible option</li>
            <li>Priority claims service</li>
          </ul>
          <el-button type="primary" @click="$router.push('/customer-login?tab=register')">Bundle Now</el-button>
        </div>
      </div>
    </section>

    <!-- About -->
    <section class="about" id="about">
      <h2 class="section-title">Why Choose HJB Insurance?</h2>
      <div class="about-grid">
        <div class="about-card">
          <span class="about-icon">⚡</span>
          <h4>Fast Claims</h4>
          <p>Most claims resolved within 48 hours. Our dedicated team works around the clock.</p>
        </div>
        <div class="about-card">
          <span class="about-icon">💰</span>
          <h4>Best Rates</h4>
          <p>Competitive pricing with flexible payment options to fit any budget.</p>
        </div>
        <div class="about-card">
          <span class="about-icon">🔒</span>
          <h4>Trusted & Secure</h4>
          <p>Over 20 years of protecting families across all 50 states.</p>
        </div>
        <div class="about-card">
          <span class="about-icon">📞</span>
          <h4>24/7 Support</h4>
          <p>Real people, real help. Available any time via phone, chat, or email.</p>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="cta">
      <h2>Ready to Get Protected?</h2>
      <p>Join thousands of satisfied customers. Sign up today — it takes less than 5 minutes.</p>
      <el-button type="primary" size="large" @click="$router.push('/customer-login?tab=register')">Create Your Account</el-button>
    </section>

    <!-- Footer -->
    <footer class="footer">
      <p>© 2026 HJB Insurance. All rights reserved.</p>
    </footer>
  </div>
</template>

<script setup>
</script>

<style scoped>
.landing { min-height: 100vh; background: var(--bg); color: var(--text-primary); }

/* Navbar */
.navbar {
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 60px; height: 68px;
  background: var(--sidebar-bg);
  position: sticky; top: 0; z-index: 100;
  border-bottom: 1px solid rgba(255,255,255,0.07);
}
.navbar-logo { display: flex; align-items: center; gap: 10px; }
.logo-icon {
  width: 36px; height: 36px; background: var(--primary);
  border-radius: 8px; display: flex; align-items: center; justify-content: center;
  color: white; font-weight: 700; font-size: 12px;
}
.logo-text { color: white; font-size: 18px; font-weight: 700; }
.navbar-links { display: flex; gap: 32px; }
.navbar-links a { color: rgba(255,255,255,0.7); text-decoration: none; font-size: 15px; transition: color 0.2s; }
.navbar-links a:hover { color: white; }
.navbar-actions { display: flex; align-items: center; gap: 10px; }

/* Hero */
.hero {
  display: flex; align-items: center; justify-content: space-between;
  padding: 80px 60px;
  background: linear-gradient(135deg, rgba(37,99,235,0.15) 0%, transparent 60%);
  min-height: 420px;
}
.hero-content { max-width: 560px; }
.hero-title { font-size: 52px; font-weight: 800; line-height: 1.15; margin-bottom: 20px; color: white; }
.hero-sub { font-size: 18px; color: var(--text-secondary); margin-bottom: 32px; line-height: 1.6; }
.hero-btns { display: flex; gap: 12px; }
.hero-visual { font-size: 120px; opacity: 0.4; }

/* Products */
.products { padding: 80px 60px; }
.section-title { font-size: 32px; font-weight: 700; color: white; text-align: center; margin-bottom: 48px; }

.product-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }

.product-card {
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: 16px;
  padding: 32px;
  display: flex; flex-direction: column; gap: 16px;
  position: relative;
  transition: transform 0.2s, border-color 0.2s;
}
.product-card:hover { transform: translateY(-4px); border-color: var(--primary); }
.product-card.featured { border-color: var(--primary); }

.product-badge {
  position: absolute; top: 16px; right: 16px;
  background: var(--primary); color: white;
  font-size: 11px; font-weight: 700;
  padding: 3px 10px; border-radius: 20px;
}
.product-icon { font-size: 40px; }
.product-card h3 { font-size: 20px; font-weight: 700; color: white; }
.product-card p { color: var(--text-secondary); font-size: 14px; line-height: 1.6; }
.product-card ul { list-style: none; padding: 0; display: flex; flex-direction: column; gap: 8px; flex: 1; }
.product-card ul li { color: var(--text-secondary); font-size: 14px; padding-left: 16px; position: relative; }
.product-card ul li::before { content: '✓'; position: absolute; left: 0; color: var(--primary); font-weight: 700; }

/* About */
.about { padding: 80px 60px; background: rgba(255,255,255,0.02); }
.about-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 24px; }
.about-card {
  background: var(--card-bg); border: 1px solid var(--border);
  border-radius: 12px; padding: 28px; text-align: center;
}
.about-icon { font-size: 36px; display: block; margin-bottom: 12px; }
.about-card h4 { font-size: 16px; font-weight: 700; color: white; margin-bottom: 8px; }
.about-card p { color: var(--text-secondary); font-size: 13px; line-height: 1.6; }

/* CTA */
.cta {
  padding: 80px 60px; text-align: center;
  background: linear-gradient(135deg, rgba(37,99,235,0.2) 0%, transparent 60%);
}
.cta h2 { font-size: 36px; font-weight: 700; color: white; margin-bottom: 12px; }
.cta p { color: var(--text-secondary); font-size: 16px; margin-bottom: 28px; }

/* Footer */
.footer { padding: 24px 60px; text-align: center; border-top: 1px solid var(--border); }
.footer p { color: var(--text-secondary); font-size: 13px; }
</style>
