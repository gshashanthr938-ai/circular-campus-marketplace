// Run only against a disposable test database. Creates accounts/listings and purchases.
import assert from 'node:assert/strict';
import fs from 'node:fs';
const base=process.env.TEST_BASE_URL;
if(!base) throw Error('Set TEST_BASE_URL to the isolated test app, e.g. http://localhost:8090');
function client(){
 const cookies=new Map();let token='';
 return {cookies,
 async request(path, data, csrf=true){
  const headers={Cookie:[...cookies].map(([k,v])=>`${k}=${v}`).join('; ')};
  const opts={headers,redirect:'manual'};
  if(data){opts.method='POST';headers['Content-Type']='application/x-www-form-urlencoded';opts.body=new URLSearchParams({...data,...(csrf?{csrfToken:token}:{})});}
  const res=await fetch(base+path,opts);
  for(const c of res.headers.getSetCookie()){const pair=c.split(';')[0],i=pair.indexOf('=');cookies.set(pair.slice(0,i),pair.slice(i+1));}
  const html=await res.text();token=html.match(/name="csrfToken" value="([^"]+)"/)?.[1]||token;
  assert.ok(res.status<500,`${path} returned ${res.status}`);
  return {html,status:res.status,location:res.headers.get('location')};
 },
 async requestMultipart(path,data){
  const headers={Cookie:[...cookies].map(([k,v])=>`${k}=${v}`).join('; ')};
  const body=new FormData();for(const [k,v] of Object.entries({...data,csrfToken:token}))body.append(k,v);
  body.append('photo1',new Blob([fs.readFileSync('src/main/webapp/img/photos/notebook.jpg')],{type:'image/jpeg'}),'notebook.jpg');
  const res=await fetch(base+path,{method:'POST',headers,body,redirect:'manual'});
  for(const c of res.headers.getSetCookie()){const pair=c.split(';')[0],i=pair.indexOf('=');cookies.set(pair.slice(0,i),pair.slice(i+1));}
  const html=await res.text();token=html.match(/name="csrfToken" value="([^"]+)"/)?.[1]||token;
  assert.ok(res.status<500,`${path} returned ${res.status}`);return {html,status:res.status,location:res.headers.get('location')};
 }};
}
const seller=client(), buyer=client(), other=client(), stamp=Date.now();
async function register(c,label){await c.request('/register');const r=await c.request('/register',{name:label,email:`${label}${stamp}@example.edu`,phone:'+91 98765 43210',password:'test-password-123'});assert.equal(r.status,302);await c.request('/browse');}
async function create(title,price,category='Stationery'){await seller.request('/sell');assert.equal((await seller.requestMultipart('/sell',{title,description:'Integration test listing',category,condition:'Good',price:String(price)})).status,302);const h=(await seller.request('/browse?search='+encodeURIComponent(title))).html;return h.match(/name="id" value="(\d+)"/)[1];}
await register(seller,'Seller');await register(other,'Other');
const title='Submission-pencil-'+stamp,id=await create(title,150);
await buyer.request('/browse');const oldSession=buyer.cookies.get('JSESSIONID');
assert.equal((await buyer.request('/cart',{action:'add',id,back:'/cart'},false)).status,403);
await buyer.request('/cart',{action:'add',id,back:'/cart'});assert.ok(buyer.cookies.get('guest_cart').includes(id));
await register(buyer,'Buyer');assert.notEqual(oldSession,buyer.cookies.get('JSESSIONID'));
assert.ok((await buyer.request('/cart')).html.includes(title));assert.ok(!buyer.cookies.get('guest_cart'));
console.log('PASS: CSRF rejection, registration, session renewal, guest cart migration');
await other.request('/sell');await other.request('/sell',{id,title:'Unauthorized change',category:'Books',condition:'Good',price:'1'});
assert.ok((await buyer.request('/listing?id='+id)).html.includes(title));
assert.equal((await buyer.request('/checkout',{paymentMethod:'UPI',upiId:'buyer@bank',acceptTerms:'yes'})).status,302);
const paidHistory=(await buyer.request('/history')).html;assert.ok(paidHistory.includes(title));assert.ok(paidHistory.includes('UPI'));assert.ok(paidHistory.includes('Contact unlocked'));assert.ok(paidHistory.includes('Terms accepted'));assert.ok(paidHistory.includes('Seller'+stamp+'@example.edu'));
assert.ok((await buyer.request('/cart')).html.includes('Your cart is empty'));
console.log('PASS: ownership protection, uploaded photo, UPI payment, purchase history, cart clearing');
const raceId=await create('Concurrent-item-'+stamp,100);
await buyer.request('/cart');await buyer.request('/cart',{action:'add',id:raceId});
await other.request('/cart');await other.request('/cart',{action:'add',id:raceId});
const races=await Promise.all([buyer.request('/checkout',{paymentMethod:'UPI',upiId:'buyer@bank',acceptTerms:'yes'}),other.request('/checkout',{paymentMethod:'NET_BANKING',bankCode:'HDFC',acceptTerms:'yes'})]);
assert.equal(races.filter(r=>r.location==='/history').length,1);
const raceLoser=races[0].location==='/history'?other:buyer;
assert.ok((await raceLoser.request('/notifications')).html.includes('waitlist'));
console.log('PASS: concurrent buyers produce one purchase and notify the waitlisted buyer');
const expensive=await create('Payment-check-item-'+stamp,5000,'Electronics');
const poor=client();await register(poor,'Poor');await poor.request('/cart',{action:'add',id:expensive});
assert.equal((await poor.request('/checkout',{paymentMethod:'UPI',upiId:'invalid',acceptTerms:'yes'})).location,'/cart');assert.ok((await poor.request('/cart')).html.includes('valid UPI ID'));
console.log('PASS: invalid payment data cannot claim the listing');
await seller.request('/sell');
const blocked=await seller.requestMultipart('/sell',{title:'Vape kit',description:'Prohibited campus item',category:'Electronics',condition:'Good',price:'500'});
assert.equal(blocked.status,200);assert.ok(blocked.html.includes('prohibited'));
console.log('PASS: prohibited items are rejected before publication');
await seller.request('/my-listings');await seller.request('/my-listings',{action:'delete',id:expensive});
assert.ok((await poor.request('/cart')).html.includes('No longer available'));
await poor.request('/cart',{action:'remove',id:expensive});assert.ok((await poor.request('/cart')).html.includes('Your cart is empty'));
await buyer.request('/profile');await buyer.request('/logout',{});assert.equal((await buyer.request('/profile')).location,'/login');
for(const route of ['/browse','/login','/register','/cart'])assert.equal((await buyer.request(route)).status,200);
console.log('PASS: removal with existing cart reference, stale-item removal, logout, public pages');
const html=(await buyer.request('/browse')).html;
const imgs=[...new Set([...html.matchAll(/src="(\/img\/[^\"]+)"/g)].map(m=>m[1]))];
for(const img of imgs)assert.equal((await fetch(base+img)).status,200,img);
console.log(`PASS: ${imgs.length} images available. HTTP integration checks complete.`);
