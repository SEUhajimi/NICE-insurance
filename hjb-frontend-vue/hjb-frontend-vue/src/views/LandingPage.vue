<template>
  <div class="landing">

    <!-- Navbar -->
    <header class="navbar">
      <div class="nav-inner">
        <div class="navbar-logo">
          <div class="logo-icon">HJB</div>
          <span class="logo-text">Insurance</span>
        </div>
        <nav class="navbar-links">
          <a href="#products">Products</a>
          <a href="#how-it-works">How It Works</a>
          <a href="#why-us">Why Us</a>
          <a href="#contact">Contact</a>
        </nav>
        <div class="navbar-actions">
          <a class="nav-link-btn" @click="$router.push('/customer-login')">Sign In</a>
          <button class="btn-primary-nav" @click="$router.push('/customer-login?tab=register')">Get a Quote</button>
          <a class="nav-employee" @click="$router.push('/login')">Employee Portal →</a>
        </div>
      </div>
    </header>

    <!-- Hero -->
    <section class="hero">
      <div class="hero-inner">
        <div class="hero-content">
          <div class="hero-badge">Trusted by 50,000+ Families</div>
          <h1 class="hero-title">
            Insurance That Works<br />
            <span class="hero-accent">As Hard As You Do</span>
          </h1>
          <p class="hero-sub">
            HJB Insurance offers comprehensive auto and home coverage with fast claims, competitive rates, and real human support—available 24/7.
          </p>
          <div class="hero-btns">
            <button class="btn-hero-primary" @click="$router.push('/customer-login?tab=register')">
              Get Your Free Quote
            </button>
            <button class="btn-hero-secondary" @click="$router.push('/customer-login')">
              View My Policies
            </button>
          </div>
          <div class="hero-trust">
            <span>✓ No hidden fees</span>
            <span>✓ Cancel anytime</span>
            <span>✓ 48-hr claims</span>
          </div>
        </div>
        <div class="hero-graphic">
          <div class="hero-card hc1">
            <div class="hc-icon">🚗</div>
            <div class="hc-label">Auto Coverage</div>
            <div class="hc-val">From $800/yr</div>
          </div>
          <div class="hero-card hc2">
            <div class="hc-icon">🏠</div>
            <div class="hc-label">Home Coverage</div>
            <div class="hc-val">From $600/yr</div>
          </div>
          <div class="hero-card hc3">
            <div class="hc-icon">🛡️</div>
            <div class="hc-label">Bundle &amp; Save</div>
            <div class="hc-val">Up to 20% off</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Stats -->
    <section class="stats">
      <div class="stats-inner">
        <div class="stat-item">
          <div class="stat-num">20+</div>
          <div class="stat-label">Years of Experience</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-num">50K+</div>
          <div class="stat-label">Families Protected</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-num">98%</div>
          <div class="stat-label">Customer Satisfaction</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-num">48hr</div>
          <div class="stat-label">Average Claims Resolution</div>
        </div>
      </div>
    </section>

    <!-- Products -->
    <section class="products" id="products">
      <div class="section-inner">
        <div class="section-label-tag">OUR COVERAGE</div>
        <h2 class="section-title">Choose the Right Protection</h2>
        <p class="section-sub">Whether you're protecting your car, your home, or both—we have a plan that fits your life and your budget.</p>

        <div class="product-grid">
          <div class="product-card">
            <div class="product-header auto">
              <span class="product-icon-lg">🚗</span>
              <div>
                <div class="product-type">Auto Insurance</div>
                <div class="product-from">Starting from <strong>$800</strong>/yr</div>
              </div>
            </div>
            <ul class="product-features">
              <li>Collision &amp; Comprehensive</li>
              <li>Bodily Injury Liability</li>
              <li>Uninsured Motorist Protection</li>
              <li>24/7 Roadside Assistance</li>
              <li>Rental Car Reimbursement</li>
            </ul>
            <button class="btn-product" @click="$router.push('/customer-login?tab=register')">Get Auto Quote</button>
          </div>

          <div class="product-card featured-card">
            <div class="featured-ribbon">Most Popular</div>
            <div class="product-header bundle">
              <span class="product-icon-lg">🛡️</span>
              <div>
                <div class="product-type">Auto + Home Bundle</div>
                <div class="product-from">Save up to <strong>20%</strong></div>
              </div>
            </div>
            <ul class="product-features">
              <li>All Auto Coverage Benefits</li>
              <li>All Home Coverage Benefits</li>
              <li>Single Deductible Option</li>
              <li>Priority Claims Processing</li>
              <li>Dedicated Account Manager</li>
            </ul>
            <button class="btn-product-featured" @click="$router.push('/customer-login?tab=register')">Bundle &amp; Save</button>
          </div>

          <div class="product-card">
            <div class="product-header home">
              <span class="product-icon-lg">🏠</span>
              <div>
                <div class="product-type">Home Insurance</div>
                <div class="product-from">Starting from <strong>$600</strong>/yr</div>
              </div>
            </div>
            <ul class="product-features">
              <li>Dwelling &amp; Structure Coverage</li>
              <li>Personal Property Protection</li>
              <li>Personal Liability</li>
              <li>Loss of Use Coverage</li>
              <li>Natural Disaster Add-ons</li>
            </ul>
            <button class="btn-product" @click="$router.push('/customer-login?tab=register')">Get Home Quote</button>
          </div>
        </div>
      </div>
    </section>

    <!-- How It Works -->
    <section class="how-it-works" id="how-it-works">
      <div class="section-inner">
        <div class="section-label-tag">SIMPLE PROCESS</div>
        <h2 class="section-title">Get Covered in 3 Easy Steps</h2>
        <div class="steps-grid">
          <div class="step-card">
            <div class="step-num">01</div>
            <h3>Create an Account</h3>
            <p>Sign up in minutes with your basic information. No paperwork, no waiting rooms.</p>
          </div>
          <div class="step-arrow">→</div>
          <div class="step-card">
            <div class="step-num">02</div>
            <h3>Choose Your Plan</h3>
            <p>Browse our flexible coverage plans and select the one that fits your needs and budget.</p>
          </div>
          <div class="step-arrow">→</div>
          <div class="step-card">
            <div class="step-num">03</div>
            <h3>Stay Protected</h3>
            <p>Your policy is active immediately. Manage everything from your personal dashboard.</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Why Us -->
    <section class="why-us" id="why-us">
      <div class="section-inner why-inner">
        <div class="why-text">
          <div class="section-label-tag">WHY HJB</div>
          <h2 class="section-title left">Built Around Your Peace of Mind</h2>
          <p class="section-sub left">We believe insurance should be simple, transparent, and responsive. That's why we built HJB from the ground up with you in mind.</p>
          <div class="why-points">
            <div class="why-point">
              <div class="why-icon">⚡</div>
              <div>
                <strong>Fast Claims Processing</strong>
                <p>Most claims are resolved within 48 hours. File online, track in real time.</p>
              </div>
            </div>
            <div class="why-point">
              <div class="why-icon">💰</div>
              <div>
                <strong>Transparent Pricing</strong>
                <p>No hidden fees. No surprises. Your rate is your rate—every renewal.</p>
              </div>
            </div>
            <div class="why-point">
              <div class="why-icon">📱</div>
              <div>
                <strong>Manage Everything Online</strong>
                <p>View policies, pay invoices, and track claims from your personal portal.</p>
              </div>
            </div>
            <div class="why-point">
              <div class="why-icon">🤝</div>
              <div>
                <strong>Real Human Support</strong>
                <p>Reach a live agent via phone, chat, or email—24 hours a day, 7 days a week.</p>
              </div>
            </div>
          </div>
        </div>
        <div class="why-visual">
          <div class="why-card-big">
            <div class="wcb-header">Your Coverage at a Glance</div>
            <div class="wcb-row"><span>🚗 Auto Policy</span><span class="wcb-active">Active</span></div>
            <div class="wcb-row"><span>🏠 Home Policy</span><span class="wcb-active">Active</span></div>
            <div class="wcb-row"><span>📄 Next Invoice</span><span class="wcb-date">May 6, 2026</span></div>
            <div class="wcb-row"><span>💳 Last Payment</span><span class="wcb-paid">$1,400.00</span></div>
            <div class="wcb-footer">
              <div class="wcb-stat"><div class="wcb-big">2</div><div class="wcb-small">Active Policies</div></div>
              <div class="wcb-stat"><div class="wcb-big">$0</div><div class="wcb-small">Outstanding</div></div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Testimonials -->
    <section class="testimonials">
      <div class="section-inner">
        <div class="section-label-tag">CUSTOMER STORIES</div>
        <h2 class="section-title">What Our Customers Say</h2>
        <div class="testimonial-grid">
          <div class="testimonial-card">
            <div class="stars">★★★★★</div>
            <p>"Filed a claim after a fender bender and the whole thing was settled in under 2 days. Absolutely impressed by the speed and professionalism."</p>
            <div class="testimonial-author">
              <div class="author-avatar">JM</div>
              <div><strong>James Mitchell</strong><br /><span>Auto Insurance Customer</span></div>
            </div>
          </div>
          <div class="testimonial-card">
            <div class="stars">★★★★★</div>
            <p>"Bundling my auto and home insurance saved me over $400 this year. The process was super easy and everything is managed in one place."</p>
            <div class="testimonial-author">
              <div class="author-avatar">SR</div>
              <div><strong>Sarah Reynolds</strong><br /><span>Bundle Customer</span></div>
            </div>
          </div>
          <div class="testimonial-card">
            <div class="stars">★★★★★</div>
            <p>"I've been with HJB for 5 years. Great rates, honest service, and the online portal makes managing policies incredibly convenient."</p>
            <div class="testimonial-author">
              <div class="author-avatar">DK</div>
              <div><strong>David Kim</strong><br /><span>Home Insurance Customer</span></div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Banner -->
    <section class="cta-banner">
      <div class="cta-inner">
        <div>
          <h2>Start Protecting What Matters</h2>
          <p>Join over 50,000 families who trust HJB Insurance. Get your free quote today.</p>
        </div>
        <div class="cta-btns">
          <button class="btn-cta-primary" @click="$router.push('/customer-login?tab=register')">Create Free Account</button>
          <button class="btn-cta-secondary" @click="$router.push('/customer-login')">Sign In</button>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="footer" id="contact">
      <div class="footer-inner">
        <div class="footer-brand">
          <div class="footer-logo">
            <div class="logo-icon">HJB</div>
            <span class="logo-text">Insurance</span>
          </div>
          <p class="footer-tagline">Protecting families across all 50 states since 2004.</p>
          <div class="footer-contact">
            <div>📞 1-800-HJB-SAFE</div>
            <div>✉️ support@hjbinsurance.com</div>
          </div>
        </div>
        <div class="footer-links-group">
          <div class="footer-col">
            <h4>Products</h4>
            <a>Auto Insurance</a>
            <a>Home Insurance</a>
            <a>Bundle &amp; Save</a>
            <a>Claims</a>
          </div>
          <div class="footer-col">
            <h4>Company</h4>
            <a>About Us</a>
            <a>Careers</a>
            <a>Press</a>
            <a>Blog</a>
          </div>
          <div class="footer-col">
            <h4>Support</h4>
            <a>Help Center</a>
            <a>File a Claim</a>
            <a>Contact Us</a>
            <a @click="$router.push('/login')" style="cursor:pointer">Employee Login</a>
          </div>
          <div class="footer-col">
            <h4>Legal</h4>
            <a>Privacy Policy</a>
            <a>Terms of Service</a>
            <a>Cookie Policy</a>
            <a>Licenses</a>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <span>© 2026 HJB Insurance Co. All rights reserved.</span>
        <span>Licensed in all 50 states · NAIC #12345</span>
      </div>
    </footer>

  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<style scoped>
* { box-sizing: border-box; }
.landing { font-family: 'DM Sans', sans-serif; color: #1e293b; background: #fff; }

/* ── Navbar ── */
.navbar {
  position: sticky; top: 0; z-index: 100;
  background: #0f172a;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
.nav-inner {
  max-width: 1200px; margin: 0 auto;
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 32px; height: 68px;
}
.navbar-logo { display: flex; align-items: center; gap: 10px; }
.logo-icon {
  width: 36px; height: 36px; background: #2563eb;
  border-radius: 8px; display: flex; align-items: center; justify-content: center;
  color: white; font-weight: 700; font-size: 12px;
}
.logo-text { color: white; font-size: 18px; font-weight: 700; }
.navbar-links { display: flex; gap: 32px; }
.navbar-links a {
  color: rgba(255,255,255,0.65); text-decoration: none;
  font-size: 14px; font-weight: 500; cursor: pointer;
  transition: color 0.2s;
}
.navbar-links a:hover { color: white; }
.navbar-actions { display: flex; align-items: center; gap: 16px; }
.nav-link-btn {
  color: rgba(255,255,255,0.8); font-size: 14px; font-weight: 500;
  cursor: pointer; text-decoration: none; transition: color 0.2s;
}
.nav-link-btn:hover { color: white; }
.btn-primary-nav {
  background: #2563eb; color: white; border: none;
  padding: 8px 20px; border-radius: 8px; font-size: 14px;
  font-weight: 600; cursor: pointer; transition: background 0.2s;
}
.btn-primary-nav:hover { background: #1d4ed8; }
.nav-employee {
  color: rgba(255,255,255,0.4); font-size: 12px; cursor: pointer;
  text-decoration: none; transition: color 0.2s;
}
.nav-employee:hover { color: rgba(255,255,255,0.7); }

/* ── Hero ── */
.hero {
  background: linear-gradient(135deg, #0f172a 0%, #1e3a5f 50%, #0f172a 100%);
  padding: 80px 32px 100px;
}
.hero-inner {
  max-width: 1200px; margin: 0 auto;
  display: flex; align-items: center; justify-content: space-between; gap: 60px;
}
.hero-content { max-width: 560px; }
.hero-badge {
  display: inline-block;
  background: rgba(37,99,235,0.25); color: #93c5fd;
  border: 1px solid rgba(37,99,235,0.4);
  font-size: 12px; font-weight: 600; letter-spacing: 0.5px;
  padding: 5px 14px; border-radius: 20px; margin-bottom: 20px;
}
.hero-title {
  font-size: 52px; font-weight: 800; line-height: 1.12;
  color: white; margin: 0 0 20px;
}
.hero-accent { color: #60a5fa; }
.hero-sub {
  font-size: 17px; color: #94a3b8; line-height: 1.7;
  margin: 0 0 32px;
}
.hero-btns { display: flex; gap: 12px; margin-bottom: 24px; }
.btn-hero-primary {
  background: #2563eb; color: white; border: none;
  padding: 14px 28px; border-radius: 10px; font-size: 15px;
  font-weight: 600; cursor: pointer; transition: background 0.2s;
}
.btn-hero-primary:hover { background: #1d4ed8; }
.btn-hero-secondary {
  background: rgba(255,255,255,0.08); color: white;
  border: 1px solid rgba(255,255,255,0.2);
  padding: 14px 28px; border-radius: 10px; font-size: 15px;
  font-weight: 600; cursor: pointer; transition: all 0.2s;
}
.btn-hero-secondary:hover { background: rgba(255,255,255,0.14); }
.hero-trust { display: flex; gap: 20px; }
.hero-trust span { color: #64748b; font-size: 13px; }

.hero-graphic { display: flex; flex-direction: column; gap: 16px; flex-shrink: 0; }
.hero-card {
  background: rgba(255,255,255,0.07);
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: 14px; padding: 18px 24px;
  display: flex; align-items: center; gap: 16px;
  backdrop-filter: blur(10px); min-width: 220px;
}
.hc1 { transform: translateX(0); }
.hc2 { transform: translateX(20px); }
.hc3 { transform: translateX(0); }
.hc-icon { font-size: 28px; }
.hc-label { font-size: 12px; color: #94a3b8; }
.hc-val { font-size: 16px; font-weight: 700; color: white; }

/* ── Stats ── */
.stats { background: #2563eb; padding: 28px 32px; }
.stats-inner {
  max-width: 1200px; margin: 0 auto;
  display: flex; align-items: center; justify-content: space-around;
}
.stat-item { text-align: center; }
.stat-num { font-size: 32px; font-weight: 800; color: white; }
.stat-label { font-size: 13px; color: rgba(255,255,255,0.75); margin-top: 4px; }
.stat-divider { width: 1px; height: 40px; background: rgba(255,255,255,0.25); }

/* ── Shared Section Styles ── */
.section-inner { max-width: 1200px; margin: 0 auto; padding: 0 32px; }
.section-label-tag {
  display: inline-block;
  background: #eff6ff; color: #2563eb;
  font-size: 11px; font-weight: 700; letter-spacing: 1px;
  padding: 4px 12px; border-radius: 20px; margin-bottom: 16px;
}
.section-title {
  font-size: 36px; font-weight: 800; color: #0f172a;
  text-align: center; margin: 0 0 12px; line-height: 1.2;
}
.section-title.left { text-align: left; }
.section-sub {
  font-size: 16px; color: #64748b; line-height: 1.7;
  text-align: center; max-width: 560px; margin: 0 auto 48px;
}
.section-sub.left { text-align: left; margin: 0 0 32px; }

/* ── Products ── */
.products { padding: 80px 32px; background: #f8fafc; }
.product-grid { display: grid; grid-template-columns: repeat(3,1fr); gap: 24px; }
.product-card {
  background: white; border: 1px solid #e2e8f0;
  border-radius: 16px; padding: 32px;
  display: flex; flex-direction: column; gap: 20px;
  position: relative; transition: box-shadow 0.2s, transform 0.2s;
}
.product-card:hover { box-shadow: 0 8px 32px rgba(0,0,0,0.1); transform: translateY(-2px); }
.featured-card { border-color: #2563eb; box-shadow: 0 4px 24px rgba(37,99,235,0.15); }
.featured-ribbon {
  position: absolute; top: -1px; left: 50%; transform: translateX(-50%);
  background: #2563eb; color: white;
  font-size: 11px; font-weight: 700; letter-spacing: 0.5px;
  padding: 4px 16px; border-radius: 0 0 10px 10px;
}
.product-header {
  display: flex; align-items: center; gap: 16px;
  padding: 16px; border-radius: 12px;
}
.product-header.auto { background: #eff6ff; }
.product-header.home { background: #f0fdf4; }
.product-header.bundle { background: #f5f3ff; }
.product-icon-lg { font-size: 36px; }
.product-type { font-size: 16px; font-weight: 700; color: #0f172a; }
.product-from { font-size: 13px; color: #64748b; margin-top: 2px; }
.product-from strong { color: #2563eb; }
.product-features { list-style: none; padding: 0; margin: 0; display: flex; flex-direction: column; gap: 10px; flex: 1; }
.product-features li {
  font-size: 14px; color: #475569;
  padding-left: 22px; position: relative;
}
.product-features li::before {
  content: '✓'; position: absolute; left: 0;
  color: #10b981; font-weight: 700;
}
.btn-product {
  padding: 12px; border: 2px solid #2563eb; color: #2563eb;
  border-radius: 10px; font-size: 14px; font-weight: 600;
  background: white; cursor: pointer; transition: all 0.2s;
}
.btn-product:hover { background: #2563eb; color: white; }
.btn-product-featured {
  padding: 12px; background: #2563eb; color: white;
  border: none; border-radius: 10px; font-size: 14px;
  font-weight: 600; cursor: pointer; transition: background 0.2s;
}
.btn-product-featured:hover { background: #1d4ed8; }

/* ── How It Works ── */
.how-it-works { padding: 80px 32px; background: white; text-align: center; }
.steps-grid {
  display: flex; align-items: center; justify-content: center; gap: 16px;
  margin-top: 48px;
}
.step-card {
  background: #f8fafc; border: 1px solid #e2e8f0;
  border-radius: 16px; padding: 32px 28px;
  max-width: 280px; text-align: center;
}
.step-num {
  font-size: 40px; font-weight: 900; color: #e2e8f0;
  line-height: 1; margin-bottom: 16px;
}
.step-card h3 { font-size: 18px; font-weight: 700; color: #0f172a; margin: 0 0 10px; }
.step-card p { font-size: 14px; color: #64748b; line-height: 1.6; margin: 0; }
.step-arrow { font-size: 24px; color: #cbd5e1; flex-shrink: 0; }

/* ── Why Us ── */
.why-us { padding: 80px 32px; background: #f8fafc; }
.why-inner { display: flex; gap: 80px; align-items: flex-start; }
.why-text { flex: 1; }
.why-points { display: flex; flex-direction: column; gap: 24px; margin-top: 32px; }
.why-point { display: flex; gap: 16px; align-items: flex-start; }
.why-icon {
  font-size: 22px; width: 48px; height: 48px;
  background: #eff6ff; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.why-point strong { font-size: 15px; color: #0f172a; display: block; margin-bottom: 4px; }
.why-point p { font-size: 14px; color: #64748b; margin: 0; line-height: 1.6; }
.why-visual { flex-shrink: 0; width: 320px; }
.why-card-big {
  background: white; border: 1px solid #e2e8f0;
  border-radius: 20px; padding: 28px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.08);
}
.wcb-header { font-size: 14px; font-weight: 700; color: #0f172a; margin-bottom: 20px; }
.wcb-row {
  display: flex; justify-content: space-between; align-items: center;
  padding: 12px 0; border-bottom: 1px solid #f1f5f9;
  font-size: 14px; color: #475569;
}
.wcb-active { color: #10b981; font-weight: 600; font-size: 13px; }
.wcb-date { color: #f59e0b; font-weight: 600; font-size: 13px; }
.wcb-paid { color: #2563eb; font-weight: 600; font-size: 13px; }
.wcb-footer {
  display: flex; justify-content: space-around;
  margin-top: 20px; padding-top: 16px;
  border-top: 1px solid #f1f5f9;
}
.wcb-stat { text-align: center; }
.wcb-big { font-size: 28px; font-weight: 800; color: #0f172a; }
.wcb-small { font-size: 12px; color: #94a3b8; margin-top: 2px; }

/* ── Testimonials ── */
.testimonials { padding: 80px 32px; background: white; text-align: center; }
.testimonial-grid { display: grid; grid-template-columns: repeat(3,1fr); gap: 24px; margin-top: 8px; }
.testimonial-card {
  background: #f8fafc; border: 1px solid #e2e8f0;
  border-radius: 16px; padding: 28px; text-align: left;
}
.stars { color: #f59e0b; font-size: 16px; margin-bottom: 14px; letter-spacing: 2px; }
.testimonial-card p {
  font-size: 14px; color: #475569; line-height: 1.7;
  margin: 0 0 20px; font-style: italic;
}
.testimonial-author { display: flex; align-items: center; gap: 12px; }
.author-avatar {
  width: 40px; height: 40px; background: #2563eb;
  border-radius: 50%; display: flex; align-items: center;
  justify-content: center; color: white; font-size: 13px; font-weight: 700;
  flex-shrink: 0;
}
.testimonial-author strong { font-size: 14px; color: #0f172a; display: block; }
.testimonial-author span { font-size: 12px; color: #94a3b8; }

/* ── CTA Banner ── */
.cta-banner { background: linear-gradient(135deg, #1e3a5f 0%, #2563eb 100%); padding: 60px 32px; }
.cta-inner {
  max-width: 1200px; margin: 0 auto;
  display: flex; align-items: center; justify-content: space-between; gap: 40px;
}
.cta-banner h2 { font-size: 32px; font-weight: 800; color: white; margin: 0 0 8px; }
.cta-banner p { font-size: 16px; color: rgba(255,255,255,0.75); margin: 0; }
.cta-btns { display: flex; gap: 12px; flex-shrink: 0; }
.btn-cta-primary {
  background: white; color: #2563eb;
  border: none; padding: 14px 28px; border-radius: 10px;
  font-size: 15px; font-weight: 700; cursor: pointer; transition: opacity 0.2s;
}
.btn-cta-primary:hover { opacity: 0.9; }
.btn-cta-secondary {
  background: rgba(255,255,255,0.12); color: white;
  border: 1px solid rgba(255,255,255,0.3);
  padding: 14px 28px; border-radius: 10px;
  font-size: 15px; font-weight: 600; cursor: pointer; transition: background 0.2s;
}
.btn-cta-secondary:hover { background: rgba(255,255,255,0.2); }

/* ── Footer ── */
.footer { background: #0f172a; padding: 64px 32px 0; }
.footer-inner {
  max-width: 1200px; margin: 0 auto;
  display: flex; gap: 80px; padding-bottom: 48px;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
.footer-brand { max-width: 280px; }
.footer-logo { display: flex; align-items: center; gap: 10px; margin-bottom: 16px; }
.footer-tagline { font-size: 14px; color: #64748b; line-height: 1.6; margin: 0 0 20px; }
.footer-contact { display: flex; flex-direction: column; gap: 8px; }
.footer-contact div { font-size: 13px; color: #94a3b8; }
.footer-links-group { display: flex; gap: 48px; flex: 1; justify-content: flex-end; }
.footer-col { display: flex; flex-direction: column; gap: 12px; }
.footer-col h4 { font-size: 13px; font-weight: 700; color: white; letter-spacing: 0.5px; margin: 0 0 4px; }
.footer-col a { font-size: 13px; color: #64748b; text-decoration: none; cursor: pointer; transition: color 0.2s; }
.footer-col a:hover { color: #94a3b8; }
.footer-bottom {
  max-width: 1200px; margin: 0 auto;
  display: flex; justify-content: space-between;
  padding: 20px 0; font-size: 12px; color: #475569;
}
</style>
